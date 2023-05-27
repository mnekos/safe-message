package pl.mnekos.safemessage.data;

import org.jetbrains.annotations.NotNull;
import pl.mnekos.safemessage.SafeMessage;
import pl.mnekos.safemessage.data.config.Configuration;
import pl.mnekos.safemessage.data.config.ConfigurationLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class DataManager {

    private SafeMessage instance;
    private Configuration configuration;
    private DataStorage storage;

    public DataManager(@NotNull SafeMessage instance) {
        this.instance = instance;
    }
    public void loadConfiguration() throws URISyntaxException, IOException {
        ConfigurationLoader cfgLoader = new ConfigurationLoader(instance);

        cfgLoader.loadConfiguration();

        configuration = cfgLoader.getConfiguration();
    }

    public void loadData() {
        if(configuration == null) {
            throw new IllegalStateException("Configuration must be loaded before loading data.");
        }

        // TODO load data
        // storage = ...
    }

}
