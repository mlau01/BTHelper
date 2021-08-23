package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import bth.core.options.OptionException;
import bth.core.options.OptionService;

public class OptionTest {
	
	 private final String localdir = System.getenv("LOCALAPPDATA");
	 
	 @Test
	 public void createConfigFilepathTest_shouldCreateDirectory() throws OptionException, IOException {
		 Path path = Paths.get(localdir + "\\.BTHelperTest\\test.conf");
		 OptionService optionService = new OptionService(path.toString());
		 
		 String createdDirectory = optionService.createConfigFileDirectory();
		 
		 assertEquals(localdir + "\\.BTHelperTest", createdDirectory);
		 assertTrue(Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS));
		 
		 // Clean
		 Files.delete(path.getParent());
		 assertFalse(Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS));
	 }
	 
	 @Test
	 public void getConfigDirectoryPath_shouldReturnConfigDirectoryPathOrCallCreateMethodIfNotExists() throws OptionException, IOException {
		 Path path = Paths.get(localdir + "\\.BTHelperTest\\test.conf");
		 OptionService optionService = new OptionService(path.toString());
		 
		 String createdDirectory = optionService.createConfigFileDirectory();
		 
		 assertEquals(localdir + "\\.BTHelperTest", createdDirectory);
		 assertTrue(Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS));
		 
		 assertEquals(localdir + "\\.BTHelperTest", optionService.getConfigDirectoryPath());
		 
		// Clean
		Files.delete(path.getParent());
		assertFalse(Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS));
	 }
	 
	 @Test
	 public void writePropertiesFileTest_shouldWritePropertiesCorrectly() throws OptionException, IOException {
		 Path path = Paths.get(localdir + "\\.BTHelperTest\\test.conf");
		 OptionService optionService = new OptionService(path.toString());
		 Properties test = new Properties();
		 test.setProperty("test", "HelloWorld");
		 
		 optionService.writePropertiesFile(test);
		 
		 assertTrue(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
		 
		// Clean
		Files.delete(path);
		Files.delete(path.getParent());
		assertFalse(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
		assertFalse(Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS));
	 }
	 
	 @Test
	 public void loadConfigTestWithNotExistingFile_shouldCreateFileAndSaveDefaultValues() throws OptionException, IOException {
		 Path path = Paths.get(localdir + "\\.BTHelperTest\\test.conf");
		 OptionService optionService = new OptionService(path.toString());
		 
		 optionService.loadConfig();
		 
		 assertTrue(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
		 
		// Clean
		Files.delete(path);
		Files.delete(path.getParent());
		assertFalse(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
		assertFalse(Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS));
	 }
	 
	 @Test
	 public void setTest_shouldSetAnOptionAndPersistIt() throws OptionException, IOException {
		 Path path = Paths.get(localdir + "\\.BTHelperTest\\test.conf");
		 OptionService optionService = new OptionService(path.toString());
		 
		 optionService.loadConfig();
		 optionService.set("test", "Hello World!");
		 
		 OptionService optionService2 = new OptionService(path.toString());
		 optionService2.loadConfig();
		 assertEquals("Hello World!", optionService2.get("test"));
		 
		// Clean
		Files.delete(path);
		Files.delete(path.getParent());
		assertFalse(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
		assertFalse(Files.exists(path.getParent(), LinkOption.NOFOLLOW_LINKS));
	 }

}
