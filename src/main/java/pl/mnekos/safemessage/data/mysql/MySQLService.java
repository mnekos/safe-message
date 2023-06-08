package pl.mnekos.safemessage.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLService extends SQLService {

    private SQLUser user;
    private String url;


    public MySQLService(Database database, SQLUser user) {
        this.url = "jdbc:mysql://" + database.getIp() + ":" + database.getPort() + "/" + database.getName() + "?useUnicode=yes&characterEncoding=UTF-8";
        this.user = user;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user.getName(), user.getPassword());
    }

    @Override
    protected synchronized void reconnect() throws SQLException {
        if(conn == null) {
            this.conn = getConnection();
            return;
        }
        try {
            close();
        } finally {
            this.conn = getConnection();
        }
    }
}
