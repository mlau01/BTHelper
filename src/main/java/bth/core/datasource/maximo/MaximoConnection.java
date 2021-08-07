package bth.core.datasource.maximo;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bth.core.CoreManager;
import bth.core.exception.LoadBtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaximoConnection {
	
	private static final Logger log = LogManager.getLogger();
	
	private final String host;
	private final String user;
	private final String passwd;
	private URL file;
	
	private String uisessionid;
	private String csrftoken;
	
	public static final int OK = 0;
	public static final int ERROR_INVALID_TOKEN = 1;
	private static boolean toF = false;
	private static final String sql_dateFormat = "yyyy-MM-dd HH:mm:ss.S";
	private static final String maximo_dateFormat = "dd.MM.yy HH:mm";
	
	public MaximoConnection(final String p_host, final String p_user, final String p_passwd)
	{
		host = p_host;
		user = p_user;
		passwd = p_passwd;
	}
	

	
	
	enum MO{
		BUTTON_NEW("toolactions_INSERT-tbb"),
		INPUT_BTTYPE("me2099556-tb"),
		MENU_QUERY("nsq-ns"),
		INPUT_DESCRIPTION("m8ee1358-tb2"),
		PAGE_QUICKREP("quickrep"),
		CHECKBOX_ARRIVED("m875af840-cb"),
		CHECKBOX_FINISHED("mb66f3e11-cb"),
		INPUT_QUICKSEARCH("quicksearch"),
		INPUT_GEAR("m6359b6a-tb"),
		INPUT_STR("mf077d57-tb"),
		INPUT_DATEARRIVED("me0a99864-tb"),
		INPUT_DATEFINISHED("m7b088082-tb"),
		INPUT_COMMENT("m3652d31b-tb"),
		BUTTON_DURATION_NEWLINE("mcaa1cd00_bg_button_addrow-pb"),
		INPUT_DURATION_FIRSTLINE("mcaa1cd00_tdrow_[C:3]_txt-tb[R:0]"),
		BUTTON_ISSUE_SET("m1177d7f8_bg_button_listfailurecodes-pb"),
		BUTTON_COMPLETE_OK("m60bd6d91-pb"),
		BUTTON_SAVE("toolactions_SAVE-tbb"),
		LONG_OP_WAIT("longopwait"),
		ISSUE_DYLINE("mc36b05ed_tdrow_[C:0]_ttxt-lb[R:%d]");
		
		private String htmlId;
		
		MO(String p_htmlId)
		{
			htmlId = p_htmlId;
		}
		
		public String getHtmlId()
		{
			String value = htmlId;
			return value;
		}
		
		public static String getIssueComp(int index)
		{
			String issue = String.format(ISSUE_DYLINE.getHtmlId(), index);
			
			return issue;
		}
	}
	
	enum EvT{
		queryclick, click,toggle,setvalue,COMP, find, longopcheck
	}
	
	private final HttpURLConnection doRequest(final String url,
											  final String type,
											  final Map<String, String> headers,
											  final Map<String, String> params)
			throws MalformedURLException, IOException, MaximoConnectionException
	{
		HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
		
			//Configuring the connexion
		con.setRequestMethod(type);
		if(type.equals("POST"))
			con.setDoOutput(true);	
		con.setConnectTimeout(7000);
		con.setReadTimeout(40000);
		
		//Adding headers if present
		if(headers != null)
		{
			
			Iterator it = headers.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
				con.addRequestProperty(pair.getKey(), pair.getValue());
			}
		}
		
		//Adding params if present
		if(params != null)
		{
			final OutputStream os = con.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
			StringBuilder result = new StringBuilder();
			
			Iterator it = params.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
				result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
				
				if(it.hasNext()) result.append("&");
			}
	
			writer.write(result.toString());
			writer.flush();
			writer.close();
			os.close();
		}
		
		//Handling response code
		int responseCode = con.getResponseCode();

		switch(responseCode)
		{
			case HttpURLConnection.HTTP_NOT_FOUND :
				throw new MaximoConnectionException("Code 404 : Url not found");
			case HttpURLConnection.HTTP_INTERNAL_ERROR :
				throw new MaximoConnectionException("HTTP Status-Code 500: Internal Server Error.");
			default :
				log.trace("HttpConnection -> Http response: {}", responseCode);
		}
		
		return con;
		
	}

	public void login() throws MaximoConnectionException, IOException
	{	
		//Init a cookie handler
		CookieManager cooman = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cooman);
		
		//Init params
		Map<String, String> params = new HashMap<String, String>();
		params.put("j_username", user);
		params.put("j_password", passwd);
		
		//Do post
		String target = host + "/maximo/j_security_check";
		HttpURLConnection con = doRequest(target,"POST", null, params);

		//Getting the target content and set useful session variable
		InputStream content =  con.getInputStream();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		while((line = in.readLine()) != null)
		{
			if(line.contains("IFRAMEPAGE"))
			{
				URL url = extractUrl(line);
				this.uisessionid = url.getQuery().split("=")[1];
				break;
			}
			if(line.contains("CSRFTOKEN"))
			{

				this.csrftoken = line.split("\"")[1];
				break;
			}

			if(this.uisessionid != null && this.csrftoken != null) {
				break;
			}
		}
	
		in.close();
		content.close();
		con.disconnect();
		
		if(uisessionid == null || csrftoken == null) {
			throw new MaximoConnectionException("cannot retrieve uisessionid");
		}
		else {
			log.info("Loging OK");
		}
	}
	
		public final int quickrep() throws MaximoConnectionException, MalformedURLException, IOException
	{	
		int retVal = OK;
		String url = "https://maximo-prod.aca.fr/maximo/ui/?event=loadapp&value=quickrep&uisessionid=";
		url += uisessionid;
		url += "&csrftoken=";
		url += csrftoken;
		HttpURLConnection con = doRequest(url, "GET", null, null);
		InputStream content =  con.getInputStream();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		while((line = in.readLine()) != null)
		{
			if(line.contains("key=InvalidLTPAToken"))
			{
				retVal = ERROR_INVALID_TOKEN;
			}
		}
		
		in.close();
		content.close();
		
		con.disconnect();
		
		//Retry connection if invalid token
		if(retVal == ERROR_INVALID_TOKEN){
			System.err.println("ERROR_INVALID_TOKEN, retry...");
			login();
			retVal = quickrep();
			if(retVal == ERROR_INVALID_TOKEN){
				System.err.println("ERROR_INVALID_TOKEN, again...");
				return ERROR_INVALID_TOKEN;
			}
		}
		else System.out.println("Loading quickrep OK");
		
		return retVal;
	}
	
	public int loadBtContext() throws MaximoConnectionException, MalformedURLException, IOException
	{	
		int retVal = 0;
		String url = "https://maximo-prod.aca.fr/maximo/ui/?event=loadapp&value=quickrep&uniqueid=6547032&uisessionid=";
		url += uisessionid;
		url += "&csrftoken=";
		url += csrftoken;
		HttpURLConnection con = doRequest(url, "GET", null, null);
		
		
		InputStream content =  con.getInputStream();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		while((line = in.readLine()) != null)
		{
			if(line.contains("key=InvalidLTPAToken"))
			{
				retVal = ERROR_INVALID_TOKEN;
			}
		}
		
		in.close();
		content.close();
		
		con.disconnect();
		
		//Retry connection if invalid token
		if(retVal == ERROR_INVALID_TOKEN){
			System.err.println("ERROR_INVALID_TOKEN, retry...");
			login();
			retVal = loadBtContext();
		}
		else System.out.println("Loading context OK");
		
		return retVal;
	}
	
	/**
	 * Make an event and send the request
	 * @param eventValue
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws MaximoConnectionException
	 */
	private final HttpURLConnection doEvent(EvT eventType, String componentId, String eventValue)
			throws MalformedURLException, IOException, MaximoConnectionException
	{
		//Init params
				Map<String, String> params = new HashMap<String, String>();
				params.put("responsetype", "text/xml");
				params.put("requesttype", "SYNC");
				params.put("localStorage", "true");
				params.put("currentfocus", componentId);
				params.put("scrollleftpos", "0");
				params.put("scrolltoppos", "0");
				params.put("uisessionid", uisessionid);
				params.put("csrftoken", csrftoken);
				String events = "[{\"type\":\"" + eventType + "\",";
				events += "\"targetId\":\"" + componentId + "\",";
				events += "\"value\":\"" + eventValue + "\",";
				events += "\"requestType\":\"SYNC\",";
				events += "\"csrftokenholder\":\"" + csrftoken + "\"}]";
				params.put("events", events);
				
				HttpURLConnection con = doRequest(host + "/maximo/ui/maximo.jsp", "POST", null, params);
				
				return con;
	}

	
	private void printResponse(HttpURLConnection con) throws IOException
	{
		InputStream response =  con.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(response));
		String line;
		
		while((line = in.readLine()) != null)
		{
			System.out.println(line);
		}
		
	
		in.close();
		response.close();
	}
	
	private void printToFile(HttpURLConnection con, String filepath) throws IOException
	{
		File save = new File(filepath);
		BufferedWriter writer = Files.newBufferedWriter(save.toPath());

		InputStream response =  con.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(response));
		String line;
		
		while((line = in.readLine()) != null)
		{
			writer.write(line);
		}
		
		writer.close();
		in.close();
		response.close();
	}
	
	public void doPrev(String[] args) throws MalformedURLException, IOException, MaximoConnectionException, InterruptedException
	{
		HttpURLConnection con;
		String start = args[0];
		String desc = args[1];
		
		con = doEvent(EvT.click, MO.BUTTON_NEW.htmlId, "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		
		con = doEvent(EvT.setvalue, MO.INPUT_DESCRIPTION.htmlId, desc);
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		
		con = doEvent(EvT.setvalue, MO.INPUT_BTTYPE.htmlId, "PREVENTIF");
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		
		String gear;
		if(desc.contains("T2")) gear = "8770/12";
		else if(desc.contains("E2")) gear = "8770/31";
		else gear = "8770/02";
		con = doEvent(EvT.setvalue, MO.INPUT_GEAR.htmlId, gear);
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		
		con = doEvent(EvT.setvalue, MO.INPUT_STR.htmlId, "UNISYS");
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		
		con = doEvent(EvT.click, MO.BUTTON_DURATION_NEWLINE.htmlId, "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		
		con = doEvent(EvT.setvalue, MO.INPUT_DURATION_FIRSTLINE.htmlId, start);
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		
		con = doEvent(EvT.COMP, MO.PAGE_QUICKREP.getHtmlId(), "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(750, 1000));
		con = doEvent(EvT.click, MO.BUTTON_COMPLETE_OK.getHtmlId(), "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 1500));
		con = doEvent(EvT.longopcheck, MO.LONG_OP_WAIT.htmlId, "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 1500));
		
		con.disconnect();
		
	}

	/**
	 * Load the target bt and get some information about checkbox
	 * @param btid
	 * @return boolean[] checkArrived at position 0, true if checked, false if not. checkFinished at position 1, same
	 * @throws MaximoConnectionException
	 * @throws IOException
	 */
	public boolean[] loadBtInfo(String btid) throws MaximoConnectionException, IOException, LoadBtException {
		boolean[] result = null;
		boolean arrivedFound = false;
		boolean finishedFound = false;
		boolean checkArrived = true;
		boolean checkFinished = true;

		//Search BT
		HttpURLConnection con = doEvent(EvT.setvalue, MO.INPUT_QUICKSEARCH.htmlId, btid);
		//con = doEvent(EvT.find, MO.INPUT_QUICKSEARCH.htmlId, "");

		InputStream response =  con.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(response));
		String line;

		//Search if checkbox "Arrivé sur site" and "Remise en service" are checked or not
		Pattern p_da = Pattern.compile(".*formatCalendar\\('" + MO.INPUT_DATEARRIVED.getHtmlId() + "',html.decodeEntities\\('\\d.*");
		Pattern p_df = Pattern.compile(".*formatCalendar\\('" + MO.INPUT_DATEFINISHED.getHtmlId() + "',html.decodeEntities\\('\\d.*");

		//File save = new File(btid + "_find.html");
		//BufferedWriter writer = Files.newBufferedWriter(save.toPath());

		while((line = in.readLine()) != null)
		{
			Matcher m_da = p_da.matcher(line);
			if(m_da.matches()) {
				log.info("Arrived checkbox already enable");
				arrivedFound = true;
				checkArrived = false;
			}
			Matcher m_df = p_df.matcher(line);
			if(m_df.matches())  {
				log.info("Finish checkbox already enable");
				finishedFound = true;
				checkFinished = false;
			}
			if(arrivedFound && finishedFound) {
				break;
			}
		}
		//writer.close();

		in.close();
		response.close();

		return new boolean[] {checkArrived, checkFinished};
	}
	
	public void fillBt(String btid,
					   String gear,
					   String btDate,
					   String desc,
					   String newDesc,
					   String travelTime,
					   String duration,
					   String issue,
					   String comment)
			throws MalformedURLException, IOException, MaximoConnectionException, InterruptedException
	{

		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));


		//Increment dates
		String dateArrived = incrementDate(btDate, sql_dateFormat, travelTime);
		String dateFinished = incrementDate(dateArrived, maximo_dateFormat, duration);

		//Set new desc if the BTHelper input is filled
		if( ! desc.equals(newDesc)) {
			con = doEvent(EvT.setvalue, MO.INPUT_DESCRIPTION.getHtmlId(), newDesc);
			if(toF) printToFile(con, btid + "_newdesc.html");
		}
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));

		//Check input "Arrivé sur site" if not checked
		if(checkArrived) { 
			con = doEvent(EvT.toggle, MO.CHECKBOX_ARRIVED.htmlId, "");
			if(toF) printToFile(con, btid + "_check_arrived.html");
			Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		}

		//Set input "Date d'arrivé sur site"
		con = doEvent(EvT.setvalue, MO.INPUT_DATEARRIVED.htmlId, dateArrived);
		if(toF) printToFile(con, btid + "_set_arrived.html");
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));

		//Check input "Remise en service" if not checked
		if(checkFinished) {
			con = doEvent(EvT.toggle, MO.CHECKBOX_FINISHED.htmlId, "");
			if(toF) printToFile(con, btid + "_check_finished.html");
			Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		}

		//Set input "Date de remise en service"
		con = doEvent(EvT.setvalue, MO.INPUT_DATEFINISHED.htmlId, dateFinished);
		if(toF) printToFile(con, btid + "_set_finished.html");
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
	
	
		//Set duration input
		if(Integer.valueOf(duration) < 10) duration = "0:0" + duration;
		else duration = "0:" + duration;
		con = doEvent(EvT.click, MO.BUTTON_DURATION_NEWLINE.htmlId, "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		con = doEvent(EvT.setvalue, MO.INPUT_DURATION_FIRSTLINE.htmlId, duration);
		Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));

		//Set issue input
		int[] issueMap = Maxi.getIssueMap(gear, issue);
		if(issueMap != null) {
			con = doEvent(EvT.click, MO.BUTTON_ISSUE_SET.htmlId,"");
			Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
			con = doEvent(EvT.click, MO.getIssueComp(issueMap[0]), "");
			if(issueMap[1] != Maxi.NONE) {
			Thread.sleep(ThreadLocalRandom.current().nextInt(700, 950));
			con = doEvent(EvT.click, MO.getIssueComp(issueMap[1]), "");
			}
			Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1050));
		} else System.out.println("Equip code not found");

		//Set comment if the BTHelper input is filled
		if( ! comment.isEmpty()) con = doEvent(EvT.setvalue, MO.INPUT_COMMENT.getHtmlId(), comment);
		
		//con = doEvent(EvT.click, MO.BUTTON_SAVE.htmlId, "");
		con = doEvent(EvT.COMP, MO.PAGE_QUICKREP.getHtmlId(), "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(750, 1000));
		con = doEvent(EvT.click, MO.BUTTON_COMPLETE_OK.getHtmlId(), "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 1500));
		con = doEvent(EvT.longopcheck, MO.LONG_OP_WAIT.htmlId, "");
		Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 1500));
		
		con.disconnect();
		
		
	}
	
	public static final String incrementDate(String date, String input_dateFormat, String minuteInc)
	{
		java.util.Date formatedDate = null;
		long millisecondInc = Long.valueOf(minuteInc) * 60 * 1000;
		
		try {
			formatedDate = new SimpleDateFormat(input_dateFormat).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		
		long millis = formatedDate.getTime() + millisecondInc;
		
		Date newDate = new Date(millis);
		SimpleDateFormat dateFormat = new SimpleDateFormat(maximo_dateFormat);
		String value = dateFormat.format(newDate);
		
		return value;
	}
	
	public void btunisys() throws MaximoConnectionException, MalformedURLException, IOException
	{

		HttpURLConnection con = doEvent(EvT.queryclick, MO.MENU_QUERY.htmlId, "queryMenuItem_2");
		
		InputStream content =  con.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		
		while((line = in.readLine()) != null)
		{
			//System.out.println(line);
			if(line.contains("<a id=\"m6a7dfd2f-lb4\""))
			{
				this.file = extractUrl(line);
				System.out.println("Found Download URL, extracting...");
				break;
			}
		}
		
	
		in.close();
		content.close();
		con.disconnect();
		
		if(file == null)
			throw new MaximoConnectionException("cannot retrieve file url");
	}
	
	public final String getFile() throws MaximoConnectionException, MalformedURLException, IOException
	{
		String contentString = "";
		String url = file.toString();
		url = url.replaceAll("&amp;", "&");
		url = url.replace("&quot;)", "");
		HttpURLConnection con = doRequest(url, "GET", null, null);
		
		InputStream content =  con.getInputStream();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		
		while((line = in.readLine()) != null)
		{
			contentString += line;
		}
		
	
		in.close();
		content.close();
		con.disconnect();
		
		return contentString;
	}
	
	private final URL extractUrl(final String urlToDecode) throws MalformedURLException
	{
		Pattern urlPattern = Pattern.compile(
		        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
		                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
		                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
		        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		
		Matcher matcher = urlPattern.matcher(urlToDecode);
		int matchStart = 0;
		int matchEnd = 0;
		while (matcher.find()) {
		    matchStart = matcher.start(1);
		    matchEnd = matcher.end();
		}
		
		return new URL(urlToDecode.substring(matchStart, matchEnd));
	}
	
	public static void main(String[] args) throws MaximoConnectionException, IOException, InterruptedException
	{
		//System.out.print(incrementDate("2019-07-31 23:50:00.0", "10"));
		 
		
	}
	 
}
