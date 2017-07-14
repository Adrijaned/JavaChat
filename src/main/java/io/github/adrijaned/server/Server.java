package io.github.adrijaned.server;

import io.github.adrijaned.shared.RSA;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adrijaned on 9.7.17.
 * Main Server's class
 */
public class Server {
    public static void main(String[] args) throws IOException {
        RSA encryption = new RSA();
        String loginFiles = args[0];
        int port = Integer.valueOf(args[1]);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Map<String, MessageListener> clients = new ConcurrentHashMap<>();
            Authentication userAuth = new Authentication(loginFiles);
            ServerConsole serverConsole = new ServerConsole(clients);
            Thread thread = new Thread(serverConsole);
            thread.setDaemon(true);
            thread.start();
            //noinspection InfiniteLoopStatement - Will be stopped externally
            while (true) {
                Socket socket = serverSocket.accept();
                Thread t = new Thread(new ConversationRunnable(socket, clients, encryption, userAuth));
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
