package pl.mnekos.safemessage.data;

import pl.mnekos.safemessage.SafeMessage;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SerializationDataStorage implements DataStorage {

    private SafeMessage instance;
    private String dataPath;

    private File file;
    private Map<Partner, List<Message>> conversations;
    private ExecutorService savingThread;


    public SerializationDataStorage(SafeMessage instance, String dataPath) {
        this.instance = instance;
        this.dataPath = dataPath;
    }
    @Override
    public void openConnection() {
        file = new File(dataPath);
        savingThread = Executors.newSingleThreadExecutor();
    }

    @Override
    public void closeConnection() {
        savingThread.shutdown();
        saveData();
    }

    @Override
    public void loadData() {
        if(file.exists()) {
            try(FileInputStream fileIn = new FileInputStream(file)) {
                try(ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
                    conversations = (Map<Partner, List<Message>>) objectIn.readObject();
                }
            } catch (ClassNotFoundException | IOException e) {
                instance.getBc().error(e);
            }
        } else {
            conversations = new HashMap<>();
        }

        savingThread.submit((Runnable) () -> {
            while(true) {
                try {
                    Thread.sleep(instance.getConfiguration().getSavePeriod());
                } catch (InterruptedException e) {}

                if(conversations != null) {
                    saveData();
                }
            }
        });
    }

    @Override
    public Map<Partner, List<Message>> getMessages() {
        return conversations;
    }

    @Override
    public Partner getLastPartner() {
        for(Partner partner : getPartners()) {
            if(partner.isLast()) {
                return partner;
            }
        }

        return null;
    }

    @Override
    public void setLastPartner(Partner partner) {
        Partner last = getLastPartner();

        if(last != null) last.setLast(false);
        partner.setLast(true);
    }

    @Override
    public Set<Partner> getPartners() {
        return conversations.keySet();
    }

    @Override
    public Partner addPartner(String ip, String name, SecretKey secretKey) {
        Partner partner = new Partner(nextId(), ip, name, secretKey, false);

        conversations.put(partner, new ArrayList<>());

        return partner;
    }

    @Override
    public void deletePartner(Partner partner) {
        conversations.remove(partner);
    }

    @Override
    public void setData(Partner partner) {}

    @Override
    public void logMessage(Message message) {
        conversations.get(message.getPartner()).add(message);
    }

    private synchronized void saveData() {
        try(FileOutputStream fileOut = new FileOutputStream(file)) {
            try(ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                objectOut.writeObject(conversations);
            }
        } catch (IOException e) {
            instance.getBc().error(e);
        }
    }

    private int nextId() {
        int id = 0;

        for(Partner partner : getPartners()) {
            int partnerId = partner.getId();

            if(id < partnerId) {
                id = partnerId;
            }
        }

        return id + 1;
    }
}
