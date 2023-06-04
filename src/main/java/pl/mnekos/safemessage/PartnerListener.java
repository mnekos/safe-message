package pl.mnekos.safemessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PartnerListener implements Runnable {

    private SafeMessage instance;

    public PartnerListener(SafeMessage instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        int port = instance.getConfiguration().getMessagingPort();

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            instance.getCloseables().add(serverSocket);

            while (true) {
                Socket socket = serverSocket.accept();
                InetAddress address = socket.getInetAddress();

                try (InputStream inputStream = socket.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        String message = new String(buffer, 0, bytesRead);
                        instance.getMessageHandler().handleMessage(address, message);
                    }
                } catch (IOException e) {
                    instance.getBc().error(e);
                }

                socket.close();
            }

        } catch (IOException e) {
            instance.getBc().error("Error occurred while listening to messages.", e);
        }
    }

}
