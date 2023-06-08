package pl.mnekos.safemessage.data.mysql;

public class Database {

    private final String ip;
    private final int port;
    private final String name;

    public Database(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }
}
