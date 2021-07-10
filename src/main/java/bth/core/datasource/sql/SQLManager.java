package bth.core.datasource.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import bth.BTHelper;
import bth.Observer;
import bth.core.datasource.DatasourceException;
import bth.core.CoreManager;
import bth.core.bt.Bt;
import bth.core.datasource.Datasource;

public class SQLManager implements Datasource {
	
	private short verboseLevel = 0;
	
	private final String protocol;
	private final String host;
	private final String database;
	private final String useCredentials;
	private final String user;
	private final String passwd;
	private final ArrayList<Observer> observers;
	private final CoreManager core;
	
	//Define the position of the data in the SQL Result depending of the select query
	private static final int WONUM = 1;
	private static final int DESC = 2;
	private static final int DATE = 3;
	private static final int ASSETNUM = 4;
	private static final int CODE = 5;
	
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss.S";

	private Connection con = null;

	public SQLManager(final CoreManager p_core, final ArrayList<Observer> p_observers, final Properties conf)
	{
		if(verboseLevel >= 2) System.out.println(this.getClass().getName() + " -> INIT");
		core = p_core;
		observers = p_observers;
		
		protocol = conf.getProperty(BTHelper.SqlProtocol);
		host = conf.getProperty(BTHelper.SqlHostname);
		database = conf.getProperty(BTHelper.SqlDatabase);
		useCredentials = conf.getProperty(BTHelper.SqlUseCredentials);
		user = conf.getProperty(BTHelper.SqlUser);
		passwd = conf.getProperty(BTHelper.SqlPasswd);
		
	}
	
	public final ResultSet getResultSet(String query) throws DatasourceException {
		if( ! scan(query)) throw new DatasourceException("Forbidden keyword detected, your request cannot be executed");
		open();
		ResultSet datas = null;
		try {
			Statement stmt = con.createStatement();
			if(verboseLevel >= 2) System.out.println(this.getClass().getName() + " -> getSqlResult() : Executing SQL request");
			datas = stmt.executeQuery(query);
		} catch (SQLException se)
		{
			throw new DatasourceException(se.getMessage());
		}
		
		return datas;
	}
	public final ArrayList<Bt> getBts(Object source) throws DatasourceException
	{
		
		ResultSet datas = getResultSet((String)source);
		
		ArrayList<Bt> arrayList = null;
		try {
			arrayList = new ArrayList<Bt>();
			while(datas.next())
			{			
				Bt bt = new Bt(datas.getString(WONUM),
						datas.getString(DATE),
						datas.getString(DESC),
						datas.getString(ASSETNUM),
						datas.getString(CODE)
					);
	
				arrayList.add(bt);
			}
		} catch (SQLException se)
		{
			throw new DatasourceException(se.getMessage());
		}
		return (arrayList);
	}
	
	/**
	 * Return true if the request is valid
	 * @param request
	 * @return
	 */
	private final boolean scan(final String request)
	{
		final String[] forbiddenWord = {"insert", "drop", "delete", "update", "write", "merge"};
		for(final String s : forbiddenWord)
		{
			if(request.toLowerCase().contains(s)) return false;
		}
		
		return true;
	}
	
	public void open() throws DatasourceException
	{	
		
		//Generation configuration
		String config = "jdbc:" + protocol.toString() + "://" + host;
		switch(protocol)
		{
			//MySQL Configuration
			case "mysql" :
				config += "/" + database;
				try {
					if(verboseLevel > 0) System.out.println(this.getClass().getName() + " -> open() : Connecting to " + config + " with user: " + user);
					con = DriverManager.getConnection(config, user, passwd);
				} catch (SQLException se)
				{
					throw new DatasourceException(se.getMessage());
				}
			break;
			
			//MS SQL Server configuration
			case "sqlserver" :				
				config += ";databaseName=" + database + ";";
				if(useCredentials.equals("true")) {
					config += "integratedSecurity=true;";
					if(verboseLevel >= 2) System.out.println("Sql Connect with windows ID using: " + config);
					try {
						con = DriverManager.getConnection(config);
					} catch (SQLException se)
					{
						throw new DatasourceException(se.getMessage());
					}
				} else {
					if(verboseLevel >= 2) System.out.println("Sql Connect using: " + config + ", user:" + user + ", password: " + passwd);
					try {
						con = DriverManager.getConnection(config, user, passwd);
					} catch (SQLException se)
					{
						throw new DatasourceException(se.getMessage());
					}
				}	
			break;
		}
		
		if(con != null)
		{
			if(verboseLevel >= 2) System.out.println(this.getClass().getName() + " -> Connection sucessfull");
		}


	}
	
	public void close() throws DatasourceException
	{
		if(con != null) {
			try {
				if(!con.isClosed())
					con.close();
			} catch (SQLException se)
			{
				throw new DatasourceException(se.getMessage());
			}
		}
	}


	@Override
	public void addObserver(Observer obs) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void notifyObserver(final String notification) {
		// TODO Auto-generated method stub
		
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
