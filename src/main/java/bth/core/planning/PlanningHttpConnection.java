package bth.core.planning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.core.exception.HttpConnectionException;
import bth.core.exception.PlanningConnectionException;

public class PlanningHttpConnection implements IPlanningConnection{
	
	//Encapsulation of HttpUrlConnection native class
	private static final Logger logger = LogManager.getLogger();

	public final PlanningContent getTargetContent(final String url, final String user, final String password, final String proxyhost) throws PlanningConnectionException
	{
		String contentString = new String();
		long lastModified; 
		HttpURLConnection con = null;
		
		//Proxy handling
		Proxy proxy = Proxy.NO_PROXY;
		if(proxyhost != null) {
			final String [] split = proxyhost.split(":");
			final String address = split[0];
			final int port = Integer.parseInt(split[1]);
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
		}
		
		try {
			con = (HttpURLConnection)new URL(url).openConnection(proxy);
		} catch (IOException e) {
			throw new PlanningConnectionException(e.getMessage());
		}
		
		//Authentication handling
		if(user != null && password != null)
		{
			String auth = user + ":" + password;
			String authEncoded = Base64.getEncoder().encodeToString(auth.getBytes());
			//String authEncoded = Base64.encode(auth.getBytes());
			con.setRequestProperty("Authorization", "Basic " + authEncoded);
		}
	
		//Configuring the connection
		try {
			con.setRequestMethod("GET");
			con.setDoOutput(true);	
			con.setConnectTimeout(7000);
			con.setReadTimeout(5000);
		} catch (ProtocolException e) {
			throw new PlanningConnectionException(e.getMessage());
		}

		//Handling response code
		int responseCode;
		try {
			responseCode = con.getResponseCode();
		} catch (IOException e) {
			logger.error("Open Connection failed: {}", e.getMessage());
			throw new PlanningConnectionException(e.getMessage());
		}
		
		if(responseCode == 401) {
			logger.info("Http response Code 401 : Authentification problem");
			throw new PlanningConnectionException("Code 401 : Authentification problem");
		}
		else if (responseCode == 404) {
			logger.info("Http response Code 404 : Url not found");
			throw new PlanningConnectionException("Code 404 : Url not found");
		}
		else {
			logger.info("Http response code: " + responseCode);
		}
		
		//Getting the target content
		try {
			InputStream content = (InputStream) con.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(content));
			String line;
			while((line = in.readLine()) != null) {
				contentString += line;
			}
		
			in.close();
			content.close();
		} catch (IOException e)
		{
			throw new PlanningConnectionException(e.getMessage());
		}
		
		lastModified = con.getLastModified();
		con.disconnect();

		
		return new PlanningContent(contentString, lastModified);
			
	}
	 
}
