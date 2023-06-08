package pl.mnekos.safemessage.data.config;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Configuration {

    private int messagingPort;
    private String dataStorageType;
    private String serializationDataPath;
    private long savePeriod;
    private String mysqlIP;
    private int mysqlPort;
    private String databaseName;
    private String mysqlUserName;
    private String mysqlUserPassword;
    private String sqliteUrl;
    private DateTimeFormatter dateFormat;
    private String syntax;
    private String myName;

    public void loadConfiguration(Properties properties) {
        messagingPort = Integer.parseInt(properties.getProperty("messaging-port", "25555"));
        dataStorageType = properties.getProperty("data-storage-type", "SERIALIZATION").toLowerCase();
        serializationDataPath = properties.getProperty("ser-data-path", "data.ser");
        savePeriod = Long.parseLong(properties.getProperty("save-period", "300000"));
        mysqlIP = properties.getProperty("mysql-database-ip", "localhost");
        mysqlPort = Integer.parseInt(properties.getProperty("mysql-database-port", "3306"));
        databaseName = properties.getProperty("mysql-database-name", "safemessage");
        mysqlUserName = properties.getProperty("mysql-user-name", "root");
        mysqlUserPassword = properties.getProperty("mysql-user-password", "");
        sqliteUrl = properties.getProperty("sqlite-url", "jdbc:sqlite:data.db");
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

    public String getMysqlIP() {
        return mysqlIP;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getMysqlUserName() {
        return mysqlUserName;
    }

    public String getMysqlUserPassword() {
        return mysqlUserPassword;
    }

    public String getSqliteUrl() {
        return sqliteUrl;
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
