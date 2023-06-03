package pl.mnekos.safemessage;

import pl.mnekos.safemessage.data.DataManager;
import pl.mnekos.safemessage.data.config.Configuration;

import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SafeMessage {

    public static final String VERSION = "1.0";

    private Broadcaster bc;
    private DataManager dataManager;
    private MessageHandler messageHandler;
    private final Set<ExecutorService> executorServices = new HashSet<>();
    private final Set<Closeable> closeables = new HashSet<>();

    private Thread MAIN_THREAD;

    public SafeMessage() {}

    public void start() {
        MAIN_THREAD = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(dataManager != null) {
                dataManager.destroy();
            }
        }));

        bc = new Broadcaster(this);
        dataManager = new DataManager(this);

        try {
            dataManager.loadConfiguration();
        } catch (URISyntaxException | IOException e) {
            bc.error(e);
        }

        dataManager.loadData();

        ExecutorService listenerExecutor = Executors.newSingleThreadExecutor();
        listenerExecutor.submit(new PartnerListener(this));
        executorServices.add(listenerExecutor);
        messageHandler = new MessageHandler(this);

        new CommandListener(this).run();
    }

    public void stop() throws IOException {
        if(!Thread.currentThread().equals(MAIN_THREAD)) {
            throw new IllegalThreadStateException("Application must be stopped by main thread.");
        }
        dataManager.destroy();
        dataManager = null;

        for(ExecutorService exec : executorServices) {
            exec.shutdown();
        }

        for (Closeable closeable : closeables) {
            closeable.close();
        }
    }

    public Set<Closeable> getCloseables() {
        return closeables;
    }

    public Configuration getConfiguration() {
        return getDataManager().getConfiguration();
    }

    public Broadcaster getBc() {
        return bc;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
