package pl.mnekos.safemessage;

import pl.mnekos.safemessage.data.Message;
import pl.mnekos.safemessage.data.Partner;

import javax.crypto.SecretKey;
import java.net.InetAddress;

public class MessageHandler {

    private SafeMessage instance;

    public MessageHandler(SafeMessage instance) {
        this.instance = instance;
    }

    public void handleMessage(InetAddress address, String message) {
        String ip = address.getHostAddress();

        Partner partner = instance.getDataManager().getPartnerByIp(ip);

        if(partner == null) {
            return;
        }

        SecretKey key = partner.getSecretKey();

        try {
            message = AESUtils.decryptAES(message, key);
        } catch (Exception e) {
            instance.getBc().error("Cannot decrypt message from partner " + partner, e);
        }

        Message message1 = new Message(false, partner, message);

        instance.getDataManager().logMessage(message1);

        instance.getBc().printMessage(message1);
    }
}
