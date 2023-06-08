package pl.mnekos.safemessage.data.mysql;

public class SQLUser {

    private final String name;
    private final String password;

    public SQLUser(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

}
