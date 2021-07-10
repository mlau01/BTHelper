package bth.core.datasource.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bth.BTHelper;
import bth.Observer;
import bth.core.datasource.DatasourceException;
import bth.core.CoreManager;
import bth.core.bt.Bt;
import bth.core.datasource.Datasource;

public class FileManager implements Datasource {

	private final Properties properties;
	private File file;
	private final short verboseLevel = 2;
	private ArrayList<Observer> observers;
	private static int WONUM = 0;
	private static int DESC = 1;
	private static int DATE = 2;
	private static int ASSETNUM = 16;
	private static int CODE = 0;
	private static String dateFormat = "dd.MM.yy HH:mm";
	private final CoreManager core;
	
	public FileManager(CoreManager p_core, final ArrayList<Observer> p_observers, final Properties p_properties)
	{
		observers = p_observers;
		properties = p_properties;
		core = p_core;
	}
	
	@Override
	public void open() throws DatasourceException {
		
	}

	@Override
	public void close() throws DatasourceException {
		file = null;		
	}

	@Override
	public ArrayList<Bt> getBts(Object source) throws DatasourceException {
		//Use source in method params for making it dynamic cause the open's method is loaded at the program execution
		
		Document doc = null;
		try {
			notifyObserver("Parsing datas...");
			if(Boolean.parseBoolean(properties.getProperty(BTHelper.FileUsed)))
			{
				file = new File((String)source);
				if(!file.exists() || !file.isFile()) throw new DatasourceException("File not found exception");
				doc = Jsoup.parse(file, "UTF-8", "");
			}
			else if(Boolean.parseBoolean(properties.getProperty(BTHelper.MaximoUsed)))
			{
				doc = Jsoup.parse((String)source);	
			}
			
			
		} catch (IOException e) {
			throw new DatasourceException(e.getMessage());
		}
		final ArrayList<String[]> rawArray = new ArrayList<String[]>();
		final Element table = doc.getElementsByTag("table").first();
		final Elements rows = table.getElementsByTag("tr");
		for(Element row : rows)
		{
			Elements cols = row.getElementsByTag("td");
			String[] tupple = new String[cols.size()];
			for(int i = 0; i < cols.size(); i++)
			{
				tupple[i] = cols.get(i).text();
			}
			rawArray.add(tupple);
		}
		
		final ArrayList<Bt> btArray = new ArrayList<Bt>();
		
		for(int i = 1; i < rawArray.size(); i++)
		{
			String[] tupple = rawArray.get(i);
			Bt bt = new Bt(tupple[WONUM],
					tupple[DATE],
					tupple[DESC],
					tupple[ASSETNUM],
					tupple[CODE]);
			
			btArray.add(bt);
		}
		
		return btArray;
		
	}
	

	@Override
	public void addObserver(Observer obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyObserver(final String notification) {
		for(Observer obs : observers)
		{
			obs.notify(notification);
		}
	}

	@Override
	public void removeObserver(Observer obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDateFormat() {
		return dateFormat;
	}

}
