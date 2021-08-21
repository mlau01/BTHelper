package bth.core.options;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import bth.BTHelper;

public class OptionService {
	private short verboseLevel = 0;
	private Properties p;

	
	public OptionService() throws OptionException
	{
		if(verboseLevel >= 2) System.out.println(this.getClass().getName() + " -> INIT");
		
		p = OptionService.getPropertiesFile(BTHelper.CONF_NAME);
		if(p == null){
			p = getDefaultProperties();
			save(p);
		}
	}
	
	private static final Properties getDefaultProperties()
	{
		final Properties np = new Properties();
		np.setProperty(BTHelper.HttpUrl, BTHelper.defaultHttpUrl);
		np.setProperty(BTHelper.HttpUser, BTHelper.defaultHttpUser);
		np.setProperty(BTHelper.HttpPasswd, BTHelper.defaultHttpPassword);
		np.setProperty(BTHelper.HttpUseProxy, BTHelper.defaultHttpUseProxy);
		np.setProperty(BTHelper.HttpUseSystemProxy, BTHelper.defaultHttpUseSystemProxy);
		np.setProperty(BTHelper.HttpProxyHost, BTHelper.defaultHttpProxyHost);
		np.setProperty(BTHelper.HttpProxyUser, BTHelper.defaultHttpProxyUser);
		np.setProperty(BTHelper.HttpProxyPassword, BTHelper.defaultHttpProxyPassword);
		np.setProperty(BTHelper.SqlUsed, BTHelper.defaultSqlUsed);
		np.setProperty(BTHelper.SqlProtocol, BTHelper.defaultSqlProtocol);
		np.setProperty(BTHelper.SqlHostname, BTHelper.defaultSqlHost);
		np.setProperty(BTHelper.SqlDatabase, BTHelper.defaultSqlDatabase);
		np.setProperty(BTHelper.SqlUseCredentials, BTHelper.defaultSqlUseCredentials);
		np.setProperty(BTHelper.SqlUser, BTHelper.defaultSqlUser);
		np.setProperty(BTHelper.SqlPasswd, BTHelper.defaultSqlPassword);
		np.setProperty(BTHelper.SqlRequest, BTHelper.defaultSqlRequest);
		np.setProperty(BTHelper.FileUsed, BTHelper.defaultFileUsed);
		np.setProperty(BTHelper.Filepath, BTHelper.defaultFilepath);
		np.setProperty(BTHelper.MaximoUsed, BTHelper.defaultMaximoUsed);
		np.setProperty(BTHelper.MaximoUrl, BTHelper.defaultMaximoUrl);
		np.setProperty(BTHelper.MaximoLogin, BTHelper.defaultMaximoLogin);
		np.setProperty(BTHelper.MaximoPassword, BTHelper.defaultMaximoPassword);
		
		np.setProperty(BTHelper.sheduleT1, BTHelper.defaultSheduleT1);
		np.setProperty(BTHelper.sheduleT1W, BTHelper.defaultSheduleT1W);
		np.setProperty(BTHelper.sheduleT1S, BTHelper.defaultSheduleT1S);
		np.setProperty(BTHelper.sheduleT2, BTHelper.defaultSheduleT2);
		np.setProperty(BTHelper.sheduleT2W, BTHelper.defaultSheduleT2W);
		np.setProperty(BTHelper.sheduleT2S, BTHelper.defaultSheduleT2S);
		
		return np;
	}
	
	public final Properties getCurrentProperties() {
		return p;
	}
	
	public void setProperties(final Properties p_p) throws OptionException
	{
		p = p_p;
		save(p);
	}
	
	public void save(final Properties p) throws OptionException {
		writePropertiesFile(p, BTHelper.CONF_FOLDER, BTHelper.CONF_NAME);
		//writePropertiesFile(p, BTHelper.CONF_FOLDER, BTHelper.CONF_NAME + "_bak" +  new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));
	}
	
	/**
	 * Write a properties file to the local user data
	 * The project name would be use that folder name
	 * @param p Properties object
	 * @param projectName Name of the folder that contains properties file
	 * @param configName Name of the file that contains properties
	 * @return String represents the config file or null if something goes wrong
	 * @throws OptionException 
	 */
	public static void writePropertiesFile(final Properties p, final String projectName, final String configName) throws OptionException {
		
		try {
			final FileOutputStream fos = new FileOutputStream(getConfigDirectoryPath()  + "/" + configName);
			p.store(fos, "Properties for: " + projectName);
			fos.close();
		} catch (IOException e)
		{
			throw new OptionException(e.getMessage());
		}
	}
	
	/**
	 * Load a properties file found in the local user data
	 * @param projectName Name of the folder that contains properties file
	 * @param configName Name of the file that contains properties
	 * @return Properties object
	 * @throws OptionException 
	 */
	public static final Properties getPropertiesFile(final String configName) throws OptionException
	{
		final String filepath = BTHelper.CONF_DIRECTORY + "/" + configName;
		final Properties p = new Properties();
		
		try {
			FileInputStream fis = new FileInputStream(filepath);
			p.load(fis);
			fis.close();
		} catch (IOException e) {
			return getDefaultProperties();
		}
		
		return p;
	}
	
	private static final String getConfigDirectoryPath() throws IOException
	{		
		final Path path = Paths.get(BTHelper.CONF_DIRECTORY);
		if(Files.exists(path, LinkOption.NOFOLLOW_LINKS))
		{
			if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) return path.toString();
			else throw new IOException("The targeted file is not valid");
		}
		else
		{
			return createConfigFilepath();
		}
	}
	
	private static String createConfigFilepath() throws IOException
	{	
		final Path path = Paths.get(BTHelper.CONF_DIRECTORY);
		
		final Path createdDir = Files.createDirectories(path);
		
		return createdDir.toString();
	}

	/**
	 * Get a property
	 * If the property does not exist, return the default properties values
	 * @param optionName
	 * @return
	 * @throws OptionException If the property name is invalid
	 */
	public String get(String optionName) throws OptionException  {
		String property = p.getProperty(optionName);
		if(property == null) {
			property = getDefaultProperties().getProperty(optionName);
			if(property == null)
			{
				throw new OptionException("Unknown property name: " + optionName);
			} else {
				p.setProperty(optionName, property);
				save(p);
			}
		}
		return property;
		
	}
}
