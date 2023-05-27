package pl.mnekos.safemessage.data;

public interface DataStorage {

    void openConnection();

    void closeConnection();

    void loadPartners();

    void saveMessage(Message message);
}
