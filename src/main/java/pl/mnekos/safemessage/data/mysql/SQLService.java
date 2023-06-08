package pl.mnekos.safemessage.data.mysql;

import java.sql.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SQLService {
    protected Connection conn = null;

    public synchronized <T> T executeUpdate(String update, Function<PreparedStatement, T> function, Object... objects) throws SQLException {
        reconnect();
        try(PreparedStatement ps = function == null ? conn.prepareStatement(update) : conn.prepareStatement(update, Statement.RETURN_GENERATED_KEYS)) {
            if (countMatches(update, '?') == objects.length) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
                ps.execute();
                T result = function == null ? null : function.apply(ps);
                ps.closeOnCompletion();
                return result;
            } else throw new IllegalArgumentException("Variable count in the query is not equal to the given parameters");
        }
        finally {
            close();
        }
    }

    public synchronized void query(String query, Consumer<ResultSet> consumer, Object... objects) throws SQLException {
        reconnect();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            if(countMatches(query, '?') == objects.length) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }

                try (ResultSet result = ps.executeQuery()) {
                    consumer.accept(result);
                }
            } else throw new IllegalArgumentException("Variable count in the query is not equal to the given parameters");
        }
        finally {
            close();
        }
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract void reconnect() throws SQLException;

    protected synchronized void close() throws SQLException {
        if(conn != null && !conn.isClosed()) conn.close();
    }

    private int countMatches(String string, char c) {
        int i = 0;

        for(char character : string.toCharArray()) {
            if(character == c) {
                i++;
            }
        }

        return i;
    }
}
