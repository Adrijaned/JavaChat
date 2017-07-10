package io.github.adrijaned;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by adrijaned on 9.7.17.
 * Main Server's class
 */
public class Server {
    public static void main(String[] args) throws IOException {
        RSA encryption = new RSA();
        try {
            ServerSocket serverSocket = new ServerSocket(25863);
            Set<MessageListenerOnServer> set = Collections.synchronizedSet(new HashSet<>());
            //noinspection InfiniteLoopStatement - Will be stopped externally
            while (true) {
                MessageListenerOnServer target = new MessageListenerOnServer(serverSocket.accept(), set, encryption);
                set.add(target);
                Thread t = new Thread(target);
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
