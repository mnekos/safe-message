package pl.mnekos.safemessage.data.mysql;

import pl.mnekos.safemessage.AESUtils;
import pl.mnekos.safemessage.Broadcaster;
import pl.mnekos.safemessage.data.DataStorage;
import pl.mnekos.safemessage.data.Message;
import pl.mnekos.safemessage.data.Partner;

import javax.crypto.SecretKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public abstract class SQLDataStorage implements DataStorage {

    protected Broadcaster bc;

    protected Map<Partner, List<Message>> conversations;

    protected SQLDataStorage(Broadcaster bc) {
        this.bc = bc;
    }

    protected abstract SQLService getSQLService();

    protected SQLService sqlService;

    @Override
    public void openConnection() {
        sqlService = getSQLService();
    }

    @Override
    public void closeConnection() {
        if(sqlService != null) {
            try {
                sqlService.close();
            } catch (SQLException e) {
                bc.error(e);
            }
        }
    }

    @Override
    public Map<Partner, List<Message>> getMessages() {
        return conversations;
    }

    @Override
    public Partner getLastPartner() {
        for(Partner partner : getPartners()) {
            if(partner.isLast()) {
                return partner;
            }
        }

        return null;
    }

    @Override
    public void setLastPartner(Partner partner) {
        Partner last = getLastPartner();

        try {

            if(last != null) {
                sqlService.executeUpdate("UPDATE `partners` SET `is_last`=false WHERE `id`=?;", null, last.getId());
                last.setLast(false);
            }
            sqlService.executeUpdate("UPDATE `partners` SET `is_last`=true WHERE `id`=?;", null, partner.getId());
            partner.setLast(true);
        } catch (SQLException e) {
            bc.error(e);
        }
    }

    @Override
    public Set<Partner> getPartners() {
        return conversations.keySet();
    }

    @Override
    public Partner addPartner(String ip, String name, SecretKey secretKey) {
        Function<PreparedStatement, Integer> function = statement -> {
            try {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            } catch (SQLException e) {
                bc.error(e);
            }

            return 0;
        };

        try {
            int id = sqlService.executeUpdate("INSERT INTO `partners`(`ip`, `name`, `secret_key`, `is_last`) VALUES (?, ?, ?, ?)", function, ip, name, AESUtils.toString(secretKey), false);

            if(id == 0) {
                bc.error("Cannot get id of created partner.");
                return null;
            }

            Partner partner = new Partner(id, ip, name, secretKey, false);
            conversations.put(partner, new ArrayList<>());
            return partner;
        } catch (SQLException e) {
            bc.error(e);
        }

        return null;
    }

    @Override
    public void deletePartner(Partner partner) {
        try {
            sqlService.executeUpdate("DELETE FROM `messages` WHERE partner_id=?", null, partner.getId());
            sqlService.executeUpdate("DELETE FROM `partners` WHERE id=?", null, partner.getId());
            conversations.remove(partner);
        } catch (SQLException e) {
            bc.error(e);
        }
    }

    @Override
    public void setData(Partner partner) {
        try {
            sqlService.executeUpdate("UPDATE `partners` SET `ip`=?,`name`=?,`secret_key`=?,`is_last`=? WHERE `id`=?", null, partner.getIp(), partner.getName(), AESUtils.toString(partner.getSecretKey()), false, partner.getId());
        } catch (SQLException e) {
            bc.error(e);
        }
    }

    @Override
    public void logMessage(Message message) {
        try {
            sqlService.executeUpdate("INSERT INTO `messages`(`from_me`, `partner_id`, `time`, `message`) VALUES (?,?,?,?)", null, message.isFromMe(), message.getPartner().getId(), message.getTime(), message.getMessage());
        } catch (SQLException e) {
            bc.error(e);
        }
    }

    protected Partner getPartner(int id) {
        for(Partner partner : getPartners()) {
            if(id == partner.getId()) {
                return partner;
            }
        }

        return null;
    }

}
