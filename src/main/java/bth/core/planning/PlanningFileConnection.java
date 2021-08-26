package bth.core.planning;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.core.exception.PlanningCharsetException;
import bth.core.exception.PlanningConnectionException;

public class PlanningFileConnection implements IPlanningConnection {
	private final static Logger logger = LogManager.getLogger();
	@Override
	public PlanningContent getTargetContent(String path, String user, String password, String proxyhost)
			throws PlanningConnectionException, PlanningCharsetException {
		String fileContent = "";
		
		path = cleanPath(path);
		
		Path file = Paths.get(path);
		File fileDetails = file.toFile();
		if( ! fileDetails.exists()) {
			throw new PlanningConnectionException("File not exists: " + path); 
		}
		try {
			for(String string : Files.readAllLines(file)) {
				logger.debug(string);
				fileContent += string;
			}
		} catch (IOException e) {
			logger.error("IOException catched: ", e.getMessage());
			throw new PlanningCharsetException(e.getMessage());
		}

		
		logger.debug("Opening file: {}, size: {}", path.toString(), fileDetails.length());
		
	   
	    
	    return new PlanningContent(fileContent, fileDetails.lastModified());
	}
	
	/**
	 * Clean the protocol at the start of the path
	 * @param path
	 * @return path cleared
	 */
	public String cleanPath(String path) {
		
		if(path.startsWith("file:")) {
			path = path.split("file:")[1];
		}
		while(path.startsWith("/") || path.startsWith("\\")) {
			path = path.subSequence(1, path.length()).toString();		
		}
		
		return path;
	}

}
