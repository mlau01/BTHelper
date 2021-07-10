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

public class OptionsManager {
	private short verboseLevel = 0;
	private Properties p;

	
	public OptionsManager() throws OptionsException
	{
		if(verboseLevel >= 2) System.out.println(this.getClass().getName() + " -> INIT");
		
		p = OptionsManager.getPropertiesFile(BTHelper.CONF_NAME);
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
		
		return np;
	}
	
	public final Properties getCurrentProperties() {
		return p;
	}
	
	public void setProperties(final Properties p_p) throws OptionsException
	{
		p = p_p;
		save(p);
	}
	
	public void save(final Properties p) throws OptionsException {
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
	 * @throws OptionsException 
	 */
	public static void writePropertiesFile(final Properties p, final String projectName, final String configName) throws OptionsException {
		
		try {
			final FileOutputStream fos = new FileOutputStream(getConfigDirectoryPath()  + "/" + configName);
			p.store(fos, "Properties for: " + projectName);
			fos.close();
		} catch (IOException e)
		{
			throw new OptionsException(e.getMessage());
		}
	}
	
	/**
	 * Load a properties file found in the local user data
	 * @param projectName Name of the folder that contains properties file
	 * @param configName Name of the file that contains properties
	 * @return Properties object
	 * @throws OptionsException 
	 */
	public static final Properties getPropertiesFile(final String configName) throws OptionsException
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
}
