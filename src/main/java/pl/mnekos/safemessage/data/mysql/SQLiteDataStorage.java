package pl.mnekos.safemessage.data.mysql;

import pl.mnekos.safemessage.AESUtils;
import pl.mnekos.safemessage.SafeMessage;
import pl.mnekos.safemessage.data.Message;
import pl.mnekos.safemessage.data.Partner;

import javax.crypto.SecretKey;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteDataStorage extends SQLDataStorage {

    private String url;

    public SQLiteDataStorage(SafeMessage instance, String url) {
        super(instance);
        this.url = url;
    }

    @Override
    public void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        super.openConnection();

        try {
            sqlService.executeUpdate("CREATE TABLE IF NOT EXISTS partners (" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  ip VARCHAR(255)," +
                    "  name VARCHAR(255)," +
                    "  secret_key VARCHAR(255)," +
                    "  is_last BOOLEAN" +
                    ");", null);
            sqlService.executeUpdate("CREATE TABLE IF NOT EXISTS messages (" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  from_me BOOLEAN," +
                    "  partner_id INT NOT NULL," +
                    "  time TIMESTAMP NOT NULL," +
                    "  message VARCHAR(10000) NOT NULL," +
                    "  FOREIGN KEY (partner_id) REFERENCES partners(id)" +
                    ");", null);
        } catch (SQLException e) {
            instance.getBc().error(e);
        }
    }

    @Override
    public void loadData() {
        try {
            conversations = new HashMap<>();

            sqlService.query("SELECT id, ip, name, secret_key, is_last FROM partners", resultSet -> {
                try {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String ip = resultSet.getString("ip");
                        String name = resultSet.getString("name");
                        SecretKey secretKey = AESUtils.generateSecretKeyFromString(resultSet.getString("secret_key"));
                        boolean isLast = resultSet.getBoolean("is_last");

                        Partner partner = new Partner(id, ip, name, secretKey, isLast);

                        conversations.put(partner, new ArrayList<>());
                    }
                } catch (SQLException e) {
                    instance.getBc().error(e);
                }
            });

            sqlService.query("SELECT from_me, partner_id, time, message FROM messages", resultSet -> {
                try {
                    while (resultSet.next()) {
                        boolean fromMe = resultSet.getBoolean("from_me");
                        int partnerId = resultSet.getInt("partner_id");
                        String timestampString = resultSet.getString("time");
                        LocalDateTime time = LocalDateTime.parse(timestampString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        String message = resultSet.getString("message");

                        Partner partner = getPartner(partnerId);

                        if(partner != null) conversations.get(partner).add(new Message(fromMe, partner, time, message));
                    }
                } catch (SQLException e) {
                    instance.getBc().error(e);
                }
            });
        } catch (SQLException e) {
            instance.getBc().error(e);
        }
    }

    @Override
    protected SQLService getSQLService() {
        return new SQLiteService(url);
    }

}
