package pl.mnekos.safemessage.data.config;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Configuration {

    private int messagingPort;
    private String dataStorageType;
    private String serializationDataPath;
    private long savePeriod;
    private String jdbcUrl;
    private String user;
    private String password;
    private DateTimeFormatter dateFormat;
    private String syntax;
    private String myName;

    public void loadConfiguration(Properties properties) {
        messagingPort = Integer.parseInt(properties.getProperty("messaging-port", "25555"));
        dataStorageType = properties.getProperty("data-storage-type", "SERIALIZATION").toLowerCase();
        serializationDataPath = properties.getProperty("ser-data-path", "data.ser");
        savePeriod = Long.parseLong(properties.getProperty("save-period", "300000"));
        jdbcUrl = properties.getProperty("data-mysql-jdbc-url", "jdbc:mysql://localhost:3306/mydatabase");
        user = properties.getProperty("data-mysql-user", "default");
        password = properties.getProperty("data-mysql-password", "default");
        dateFormat = DateTimeFormatter.ofPattern(properties.getProperty("date-format", "yyyy-MM-dd HH:mm"));
        syntax = properties.getProperty("syntax", "[%date%] %from%: %message%");
        myName = properties.getProperty("my-name", "ME");
    }

    public int getMessagingPort() {
        return messagingPort;
    }

    public String getDataStorageType() {
        return dataStorageType;
    }

    public String getSerializationDataPath() {
        return serializationDataPath;
    }

    public long getSavePeriod() {
        return savePeriod;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public DateTimeFormatter getDateFormat() {
        return dateFormat;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getMyName() {
        return myName;
    }
}
