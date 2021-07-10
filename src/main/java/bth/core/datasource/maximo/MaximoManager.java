package bth.core.datasource.maximo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import bth.BTHelper;
import bth.Observer;
import bth.core.datasource.DatasourceException;
import bth.core.CoreManager;
import bth.core.bt.Bt;
import bth.core.datasource.Datasource;
import bth.core.datasource.file.FileManager;

public class MaximoManager implements Datasource {
	
	private final Properties prop;
	private MaximoConnection max;
	private String file;
	final ArrayList<Observer> observers;
	private final FileManager fileManager;
	int retry = 0;
	private final CoreManager core;
	
	public MaximoManager(final CoreManager p_core, final ArrayList<Observer> p_observers, final Properties p_prop)
	{
		observers = p_observers;
		prop = p_prop;
		core = p_core;
		fileManager = new FileManager(core, observers, prop);
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
		
		max = new MaximoConnection(core,
				prop.getProperty(BTHelper.MaximoUrl),
				prop.getProperty(BTHelper.MaximoLogin),
				prop.getProperty(BTHelper.MaximoPassword)
		);
		
		try {
			notifyObserver("Loging into Maximo...");
			max.login();
		} catch (MaximoConnectionException | IOException e) {
			throw new DatasourceException("Error logging : " + e.getMessage());
		}
	
		
		try {
			notifyObserver("Load BT context...");
			max.quickrep();
		} catch (MaximoConnectionException | IOException e) {
			throw new DatasourceException("Error getting BT's : " + e.getMessage());
		}
		
		try {
			notifyObserver("Filtering Unisys BT's...");
			max.btunisys();
		} catch (MaximoConnectionException | IOException e) {
				e.printStackTrace();
				throw new DatasourceException("Error filtering Unisys BT's..." + e.getMessage());
		}

		try {
			notifyObserver("Processing...");
			file = max.getFile();
		} catch (MaximoConnectionException | IOException e) {
			throw new DatasourceException("Error processing : " + e.getMessage());
		}
		
		max = null;

		return fileManager.getBts(file);
	}

	@Override
	public void addObserver(Observer obs) {
		observers.add(obs);
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
		observers.remove(obs);
		
	}

	@Override
	public String getDateFormat() {
		return fileManager.getDateFormat();
	}

}
