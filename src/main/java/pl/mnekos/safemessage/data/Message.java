package pl.mnekos.safemessage.data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    private final boolean fromMe;
    private final Partner partner;
    private final LocalDateTime time;
    private final String message;

    public Message(boolean fromMe, Partner partner, LocalDateTime time, String message) {
        this.fromMe = fromMe;
        this.partner = partner;
        this.time = time;
        this.message = message;
    }

    public Message(boolean fromMe, Partner partner, String message) {
        this(fromMe, partner, LocalDateTime.now(), message);
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public boolean isFromPartner() {
        return !isFromMe();
    }

    public Partner getPartner() {
        return partner;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }


}
