package io.github.adrijaned.server;

import io.github.adrijaned.shared.RSA;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ConversationRunnable implements Runnable {
    private final Socket socket;
    private final Map<String, MessageListener> clients;
    private final RSA encryption;
    private final Authentication userAuth;

    ConversationRunnable(Socket socket, Map<String, MessageListener> clients, RSA encryption, Authentication userAuth) {
        this.socket = socket;
        this.clients = clients;
        this.encryption = encryption;
        this.userAuth = userAuth;
    }

    @Override
    public void run() {
        try {
            MessageListener listener = new MessageListener(socket, clients, encryption, userAuth);
            listener.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
