package pl.mnekos.safemessage;

import javax.crypto.SecretKey;
import java.io.OutputStream;
import java.net.Socket;

public class MessageSender {

    public static void send(String ip, int port, String message, SecretKey key) throws Exception {
        Socket socket = new Socket(ip, port);

        message = AESUtils.encryptAES(message, key);

        try(OutputStream outputStream = socket.getOutputStream()) {
            byte[] data = message.getBytes();

            outputStream.write(data);
            outputStream.flush();
        }

        socket.close();
    }

}
