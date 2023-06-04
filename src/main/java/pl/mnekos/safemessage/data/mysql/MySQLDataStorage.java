package pl.mnekos.safemessage.data.mysql;

import pl.mnekos.safemessage.SafeMessage;
import pl.mnekos.safemessage.data.DataStorage;
import pl.mnekos.safemessage.data.Message;
import pl.mnekos.safemessage.data.Partner;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLDataStorage implements DataStorage {

    private SafeMessage instance;
    private String jdbcUrl;
    private String user;
    private String password;

    public MySQLDataStorage(SafeMessage instance, String jdbcUrl, String user, String password) {
        this.instance = instance;
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }


    @Override
    public void openConnection() {

    }

    @Override
    public void closeConnection() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public Map<Partner, List<Message>> getMessages() {
        return null;
    }

    @Override
    public Partner getLastPartner() {
        return null;
    }

    @Override
    public void setLastPartner(Partner partner) {

    }

    @Override
    public Set<Partner> getPartners() {
        return null;
    }

    @Override
    public Partner addPartner(String ip, String name, SecretKey secretKey) {
        return null;
    }

    @Override
    public void deletePartner(Partner partner) {

    }

    @Override
    public void setData(Partner partner) {

    }

    @Override
    public void logMessage(Message message) {

    }
}
