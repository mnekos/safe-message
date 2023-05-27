package pl.mnekos.safemessage.data.config;

import pl.mnekos.safemessage.SafeMessage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class FileManager {

    private static final String CONFIG_FILE_NAME = "config.properties";

    private SafeMessage instance;
    private File configFile;

    public FileManager(SafeMessage instance) {
        this.instance = instance;
        this.configFile = new File(CONFIG_FILE_NAME);
    }

    // Returns true if file exists, and false if copying was needed.
    public boolean checkFiles() throws URISyntaxException, IOException {
        if(configFile.exists()) {
            return true;
        }

        Files.copy(new File(instance.getClass().getResource(CONFIG_FILE_NAME).toURI()).toPath(), configFile.toPath());
        return false;
    }

    public File getConfigFile() {
        return configFile;
    }
}
