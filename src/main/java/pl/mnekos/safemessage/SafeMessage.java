package pl.mnekos.safemessage;

import pl.mnekos.safemessage.data.DataManager;

import java.io.IOException;
import java.net.URISyntaxException;

public class SafeMessage {

    private Broadcaster bc;
    private DataManager dataManager;
    public SafeMessage() {}

    public void start() {
        bc = new Broadcaster();
        dataManager = new DataManager(this);

        try {
            dataManager.loadConfiguration();
        } catch (URISyntaxException | IOException e) {
            bc.error(e);
        }

        dataManager.loadData();
    }

    public Broadcaster getBc() {
        return bc;
    }
}
