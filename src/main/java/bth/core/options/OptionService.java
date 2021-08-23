package bth.core.options;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.BTHelper;

/**
 * This class handle properties of the application
 * Properties are saved in a file in the given path/filename
 * @author Matt
 *
 */
public class OptionService {
	private Properties p;
	private String configPath;
	private static final Logger logger = LogManager.getLogger();

	public OptionService(String p_configPath) throws OptionException
	{
		configPath = p_configPath;
		p = getPropertiesFile();
		if(p == null){
			p = getDefaultProperties();
			save(p);
		}
	}
	
	/**
	 * Create a Properties object with all default options found in BTHelper.java
	 * @return Properties
	 */
	public final Properties getDefaultProperties()
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
	
	/**
	 * Get the actual properties loaded
	 * @return
	 */
	public final Properties getCurrentProperties() {
		return p;
	}
	
	/**
	 * Set a new properties object
	 * Write the configuration file with this new properties
	 * @param p_p
	 * @throws OptionException
	 */
	public void setProperties(final Properties p_p) throws OptionException
	{
		p = p_p;
		save(p);
	}
	
	/**
	 * Save the given properties in the file
	 * @param Given properties
	 * @throws OptionException if something goes wrong with the write process
	 */
	public void save(final Properties p) throws OptionException {
		writePropertiesFile(p, BTHelper.CONF_FOLDER, BTHelper.CONF_NAME);
	}
	
	/**
	 * Write a properties file in the file system
	 * @param p Properties object to write
	 * @param projectName Name of the folder that will contains properties file
	 * @param configName Name of the file that will contains properties
	 * @throws OptionException if something goes wrong with the write process
	 */
	public void writePropertiesFile(final Properties p, final String projectName, final String configName) throws OptionException {
		
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
	public final Properties getPropertiesFile() throws OptionException
	{
		final Properties p = new Properties();
		
		try {
			FileInputStream fis = new FileInputStream(configPath);
			p.load(fis);
			fis.close();
		} catch (IOException e) {
			logger.warn("Failed to load properties file, use default properties instead");
			return getDefaultProperties();
		}
		
		return p;
	}
	
	/**
	 * Get the path directory of the configPath
	 * If it doesn't exist, create it
	 * @return
	 * @throws IOException
	 */
	public final String getConfigDirectoryPath() throws IOException
	{		
		final Path path = Paths.get(configPath);
		if(Files.exists(path, LinkOption.NOFOLLOW_LINKS))
		{
			if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
				return path.toString();
			}
			else {
				throw new IOException("The targeted file is not valid");
			}
		}
		else
		{
			return createConfigFilepath(path);
		}
	}
	
	/**
	 * Create a directory in the target path
	 * @return Directory path created
	 * @throws IOException
	 */
	public String createConfigFilepath(Path directory) throws IOException
	{	
		final Path createdDir = Files.createDirectories(directory.getParent());
		
		return createdDir.toString();
	}

	/**
	 * Get a property
	 * If the property does not exist, return the default properties values
	 * @param optionName
	 * @return
	 * @throws OptionException If the property name is unknown
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
