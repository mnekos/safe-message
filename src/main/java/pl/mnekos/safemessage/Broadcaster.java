package pl.mnekos.safemessage;

import pl.mnekos.safemessage.data.DataManager;
import pl.mnekos.safemessage.data.Message;
import pl.mnekos.safemessage.data.Partner;
import pl.mnekos.safemessage.data.config.Configuration;

import java.io.IOException;

public class Broadcaster {

    private DataManager dataManager;

    public Broadcaster(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void send(String message) {
        System.out.println(message);
    }

    public void error(Exception e) {
        e.printStackTrace(System.err);
    }

    public void error(String message) {
        System.err.println(message);
    }

    public void error(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace(System.err);
    }

    public void changeConversation(Partner newPartner) {
        Broadcaster.clearScreen();
        Broadcaster.setTitle("Conversation with " + newPartner.getName());

        dataManager.setLastPartner(newPartner);
        for(Message message : dataManager.getMessages(newPartner)) {
            printMessage(message);
        }
    }

    public void printMessage(Message message) {
        Configuration config = dataManager.getConfiguration();
        String syntax = config.getSyntax();

        syntax = syntax.replaceAll("%date%", message.getTime().format(config.getDateFormat()));
        if(message.isFromMe()) {
            syntax = syntax.replaceAll("%from%", config.getMyName());
        } else {
            syntax = syntax.replaceAll("%from%", message.getPartner().getName());
        }
        syntax = syntax.replaceAll("%message%", message.getMessage());

        send(syntax);
    }

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException | InterruptedException ex) {
            for(int i = 0; i < 2400; i++) {
                System.out.print("\b");
            }
        }
    }

    public static void setTitle(String newTitle) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "title", newTitle);
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Linux or macOS
            try {
                String command = String.format("tput reset && tput setaf 7 && tput bold && tput cup 0 0 && tput el && echo \"%s\"", newTitle);
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // else it's not supported
    }
}
