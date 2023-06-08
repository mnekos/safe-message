package pl.mnekos.safemessage.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteService extends SQLService {

    private final String url;

    public SQLiteService(String url) {
        this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    protected synchronized void reconnect() throws SQLException {
        if(conn == null) {
            conn = getConnection();
        }

        if(conn.isClosed()) {
            conn = getConnection();
        }
    }
}
