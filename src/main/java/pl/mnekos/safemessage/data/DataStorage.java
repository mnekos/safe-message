package pl.mnekos.safemessage.data;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO Write implementations for mysql and sqlite
public interface DataStorage {

    void openConnection();

    void closeConnection();

    void loadData();

    Map<Partner, List<Message>> getMessages();

    Partner getLastPartner();

    void setLastPartner(Partner partner);

    Set<Partner> getPartners();

    Partner addPartner(String ip, String name, SecretKey secretKey);

    void deletePartner(Partner partner);

    void setData(Partner partner);

    void logMessage(Message message);

}
