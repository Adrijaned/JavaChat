package io.github.adrijaned;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adrijaned on 9.7.17.
 * Main Server's class
 */
public class Server {
    public static void main(String[] args) throws IOException {
        RSA encryption = new RSA();
        try {
            ServerSocket serverSocket = new ServerSocket(25863);
            Map<String, MessageListenerOnServer> clients = new ConcurrentHashMap<>();
            Authentication userAuth = new Authentication("serverFiles/USERS.TXT");
            //noinspection InfiniteLoopStatement - Will be stopped externally
            while (true) {
                MessageListenerOnServer target = new MessageListenerOnServer(serverSocket.accept(), clients, encryption, userAuth);
                Thread t = new Thread(target);
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
