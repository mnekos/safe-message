package pl.mnekos.safemessage.data;

import pl.mnekos.safemessage.AESUtils;
import pl.mnekos.safemessage.IllegalAESKeyException;
import pl.mnekos.safemessage.SafeMessage;
import pl.mnekos.safemessage.Validate;
import pl.mnekos.safemessage.data.config.Configuration;
import pl.mnekos.safemessage.data.config.ConfigurationLoader;
import pl.mnekos.safemessage.data.mysql.Database;
import pl.mnekos.safemessage.data.mysql.MySQLDataStorage;
import pl.mnekos.safemessage.data.mysql.SQLUser;
import pl.mnekos.safemessage.data.mysql.SQLiteDataStorage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

public class DataManager {

    private SafeMessage instance;
    private Configuration configuration;
    private DataStorage storage;

    private Set<Partner> partners;
    private Map<Partner, List<Message>> currentConversations;

    public DataManager(SafeMessage instance) {
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

        switch (configuration.getDataStorageType()) {
            case "mysql":
                Database database = new Database(configuration.getMysqlIP(), configuration.getMysqlPort(), configuration.getDatabaseName());
                SQLUser user = new SQLUser(configuration.getMysqlUserName(), configuration.getMysqlUserPassword());

                storage = new MySQLDataStorage(instance.getBc(), database, user);
                instance.getBc().send("Choose MySQL storage type.");
                break;
            case "sqlite":
                storage = new SQLiteDataStorage(instance.getBc(), configuration.getSqliteUrl());
                instance.getBc().send("Choose SQLite storage type.");
                break;
            default:
                storage = new SerializationDataStorage(instance, configuration.getSerializationDataPath());
                instance.getBc().send("Choose storage storage type.");
                break;
        }

        storage.openConnection();
        storage.loadData();

        partners = storage.getPartners();

        currentConversations = storage.getMessages();
    }

    public void destroy() {
        storage.closeConnection();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Partner getLastPartner() {
        return storage.getLastPartner();
    }

    public void setLastPartner(Partner partner) {
        Validate.notNull(partner, "partner");
        storage.setLastPartner(partner);
    }

    public Partner addPartner(String ip, String name, String secretKeyString) throws UnknownHostException, IllegalAESKeyException {
        Validate.notEmpty(ip, "ip");
        InetAddress.getByName(ip); // throws exception if ip is not valid.
        Validate.hasSize(name, "name", 1, 32);
        Validate.notEmpty(secretKeyString, "secretKeyString");
        if(!AESUtils.isValidKey(secretKeyString)) throw new IllegalAESKeyException();

        return storage.addPartner(ip, name, AESUtils.generateSecretKeyFromString(secretKeyString));
    }

    public Partner getPartnerByIp(String ip) {
        if(ip == null) {
            return null;
        }

        for(Partner partner : partners) {
            if(partner.getIp().equals(ip)) {
                return partner;
            }
        }

        return null;
    }

    public Partner getPartnerByName(String name) {
        if(name == null) {
            return null;
        }

        for(Partner partner : partners) {
            if(partner.getName().equalsIgnoreCase(name)) {
                return partner;
            }
        }

        return null;
    }

    public void logMessage(Message message) {
        Validate.notNull(message, "message");
        storage.logMessage(message);
    }

    public void deletePartner(Partner partner) {
        Validate.notNull(partner, "partner");
        partners.remove(partner);
        storage.deletePartner(partner);

        if(partners.size() == 0) {
            return;
        }

        if(Objects.equals(getLastPartner(), partner)) {
            setLastPartner((Partner) partners.toArray()[0]);
        }
    }

    public void setPartnerIp(Partner partner, String ip) throws UnknownHostException {
        Validate.notNull(partner, "partner");
        InetAddress.getByName(ip);
        partner.setIp(ip);
        storage.setData(partner);
    }

    public void setPartnerName(Partner partner, String name) {
        Validate.notNull(partner, "partner");
        Validate.hasSize(name, "name", 1, 32);
        partner.setName(name);
        storage.setData(partner);
    }

    public void setPartnerSecretKey(Partner partner, String secretKey) throws IllegalAESKeyException {
        Validate.notNull(partner, "partner");
        if(!AESUtils.isValidKey(secretKey)) throw new IllegalAESKeyException();
        partner.setSecretKey(AESUtils.generateSecretKeyFromString(secretKey));
        storage.setData(partner);
    }

    public List<Message> getMessages(Partner partner) {
        Validate.notNull(partner, "partner");
        return currentConversations.get(partner);
    }

    public Collection<Partner> getPartners() {
        return new ArrayList<>(partners);
    }
}
