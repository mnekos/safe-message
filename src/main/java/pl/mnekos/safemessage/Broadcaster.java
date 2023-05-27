package pl.mnekos.safemessage;

public class Broadcaster {

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

}
