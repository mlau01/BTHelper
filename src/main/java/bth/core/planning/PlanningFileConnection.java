package bth.core.planning;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bth.core.exception.PlanningConnectionException;

public class PlanningFileConnection implements IPlanningConnection {

	@Override
	public PlanningContent getTargetContent(String path, String user, String password, String proxyhost)
			throws PlanningConnectionException {
		String fileContent = null;
		
		path = cleanPath(path);
		
		Path fichier = Paths.get(path);
		
	    try (BufferedReader reader = Files.newBufferedReader(fichier, Charset.forName("UTF-8"))) {
	      String line = null;
	      while ((line = reader.readLine()) != null) {
	        fileContent += line;
	      }
	    } catch (IOException ioe) {
	      throw new PlanningConnectionException(ioe.getMessage());
	    }
	    
	    return new PlanningContent(fileContent, fichier.toFile().lastModified());
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
