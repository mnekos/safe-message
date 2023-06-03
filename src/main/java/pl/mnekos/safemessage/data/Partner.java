package pl.mnekos.safemessage.data;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Objects;

public class Partner implements Serializable {

    private final int id;
    private String ip;
    private String name;
    private SecretKey secretKey;
    private boolean isLast;

    public Partner(int id, String ip, String name, SecretKey secretKey, boolean isLast) {
        this.id = id;
        this.ip = ip;
        this.name = name;
        this.secretKey = secretKey;
        this.isLast = isLast;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partner partner = (Partner) o;
        return id == partner.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Partner{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", secretKey=" + secretKey +
                ", isLast=" + isLast +
                '}';
    }
}
