package pl.mnekos.safemessage.data.config;

import pl.mnekos.safemessage.SafeMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationLoader {

    private FileManager manager;
    private Configuration configuration;

    public ConfigurationLoader(SafeMessage instance) {
        this.manager = new FileManager(instance);
    }

    public void loadConfiguration() throws IOException {
        manager.checkFiles();

        configuration = new Configuration();

        File file = manager.getConfigFile();

        FileInputStream inputStream = new FileInputStream(file);

        Properties properties = new Properties();
        properties.load(inputStream);

        inputStream.close();

        configuration.loadConfiguration(properties);
    }
    public Configuration getConfiguration() {
        return configuration;
    }
}
