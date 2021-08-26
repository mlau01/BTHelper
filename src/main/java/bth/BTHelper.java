package bth;

import bth.core.schedule.ScheduleCategory;

public class BTHelper {
	
	public static final String AUTHOR = ("Mathias Lauer");
	public static final String AUTHORMAIL = ("mathias.lauer.fr@gmail.com");
	public static final String APP_NAME = ("BT's Helper");
	public static final String APP_VERSION = ("2.0.1.0");
	
	public static final String CONF_FOLDER = ".BTHelper";
	public static final String CONF_NAME = "config";
	public static final String CONF_DIRECTORY = System.getenv("LOCALAPPDATA") + "/" + CONF_FOLDER;
	
	
	public static final String iconsPath = ("/icons/");
	
	//Options name --------------------------------------------------------
	public static final String HttpUrl = ("HttpUrl");
	public static final String HttpUser = ("HttpUser");
	public static final String HttpPasswd = ("HttpPasswd");
	public static final String HttpUseProxy = ("HttpUseProxy");
	public static final String HttpUseSystemProxy = ("HttpUseSystemProxy");
	public static final String HttpProxyHost = ("HttpProxyHost");
	public static final String HttpProxyUser = ("HttpProxyUser");
	public static final String HttpProxyPassword = ("HttpProxyPassword");
	
	public static final String SqlUsed = ("SqlUsed");
	public static final String SqlHostname = ("SqlHostname");
	public static final String SqlProtocol = ("SqlProtocol");
	public static final String SqlDatabase = ("SqlDatabase");
	public static final String SqlUseCredentials = ("SqlUseCredentials");
	public static final String SqlRequest = ("SqlRequest");
	public static final String SqlUser = ("SqlUser");
	public static final String SqlPasswd = ("SqlPasswd");
	
	public static final String mysql = ("mysql");
	public static final String sqlserver = ("sqlserver");
	
	public static final String FileUsed = ("FileUsed");
	public static final String Filepath = ("Filepath");
	
	public static final String MaximoUsed = ("MaximoUsed");
	public static final String MaximoUrl = ("MaximoUrl");
	public static final String MaximoLogin = ("MaximoLogin");
	public static final String MaximoPassword = ("MaximoPassword");
	
	public static final String sheduleT1 = ScheduleCategory.T1.getOptionName();
	public static final String sheduleT1W = ScheduleCategory.T1W.getOptionName();
	public static final String sheduleT1S = ScheduleCategory.T1S.getOptionName();
	public static final String sheduleT2 = ScheduleCategory.T2.getOptionName();
	public static final String sheduleT2W = ScheduleCategory.T2W.getOptionName();
	public static final String sheduleT2S = ScheduleCategory.T2S.getOptionName();
	
	public static final String Queries = "queries";
	
	//Default Options -------------------------------------------------------------------------
	public final static String defaultSqlUsed = "false";
	public final static String defaultSqlProtocol = "sqlserver";
	public final static String defaultSqlHost = "BERGAME";
	public final static String defaultSqlDatabase = "MAXIMO";
	public final static String defaultSqlUseCredentials = "true";
	public final static String defaultSqlUser = "";
	public final static String defaultSqlPassword = "";
	
	public final static String defaultFileUsed = "true";
	public final static String defaultFilepath = "C:\\Users\\";
	
	public final static String defaultMaximoUsed = "false";
	public final static String defaultMaximoUrl = "";
	public final static String defaultMaximoLogin = "unisys";
	public final static String defaultMaximoPassword = "";
	
	public final static String defaultHttpUrl = "http://nce1dbmain.free.fr";
	public final static String defaultHttpUser = "unisys";
	public final static String defaultHttpPassword = "";
	
	public final static String defaultHttpUseProxy = "true";
	public final static String defaultHttpUseSystemProxy = "false";
	public final static String defaultHttpProxyHost = "10.1.40.251:8008";
	public final static String defaultHttpProxyUser = "";
	public final static String defaultHttpProxyPassword = "";
	
	public final static String defaultSheduleT1 = "M2=(04:15:00,06:59:59);M1=(07:00:00,13:29:59);S1=(13:30:00,19:59:59);S2=(20:00:00,23:30:00)";
	public final static String defaultSheduleT1W = "M2=(04:15:00,06:59:59);M1=(07:00:00,13:29:59);S1=(13:30:00,19:59:59);S2=(20:00:00,23:30:00)";
	public final static String defaultSheduleT1S = "SM=(04:15:00,06:59:59);M1=(07:00:00,13:29:59);S1=(13:30:00,19:59:59);SS=(20:00:00,23:30:00)";
	public final static String defaultSheduleT2 = "M2=(04:30:00,11:29:59);A=(11:30:00,16:29:59);S2=(16:30:00,23:30:00)";
	public final static String defaultSheduleT2W = "SM=(04:30:00,13:59:59);SS=(14:00:00,23:30:00)";
	public final static String defaultSheduleT2S = "SM=(04:30:00,13:59:59);SS=(14:00:00,23:30:00)";
	
	public final static String defaultQueries = "";
	
	public final static String defaultSqlRequest = "SELECT WONUM, REPORTDATE, DESCRIPTION FROM WORKORDER\n"
	+ "WHERE (\n"
	+ "\tSTATUS Not Like 'ann'\n"
	+ "\tAND STATUS Not Like 'fermer'\n"
	+ "\tAND UPPER(LEAD)='UNISYS'\n"
	+ "\tAND UPPER(DESCRIPTION) NOT LIKE ('XX%')\n"
	+ "\tAND UPPER(WORKTYPE) NOT LIKE ('PREVENTIF')\n"
	+ "\tAND (\n"
		+ "\t\two21 Is Null\n"
		+ "\t\tOR ACTLABHRS = 0\n"
		+ "\t\tOR PROBLEMCODE is NULL\n"
		+ "\t\tOR (\n"
			+ "\t\t\tUPPER(DESCRIPTION) NOT LIKE ('T1 BQE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T2 BQE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T1 PTE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T2 PTE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T1 BLS%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T2 BLS%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('E2 BLS%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('E2 BQE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T1 AFF%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T2 AFF%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('E2 PTE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T2 PAS%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T1 PAS%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('E2 PAS%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T1 CRE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T2 CRE%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('E2 AFF%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('E2 DBA%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('E2 PIF%')\n"
			+ "\t\t\tAND UPPER(DESCRIPTION) NOT LIKE ('T2 PIF%')\n"
		+ "\t\t)\n"
	+ "\t)\n"
+ ")\n"
+ "ORDER BY REPORTDATE DESC\n";

}