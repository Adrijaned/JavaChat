package io.github.adrijaned;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Set;

/**
 * Created by adrijaned on 9.7.17.
 * On server, listen on client's socket and forward all incoming messages to server.
 */

public class MessageListenerOnServer implements Runnable {
    private Set<MessageListenerOnServer> setOfClients;
    private BufferedReader reader;
    private PrintWriter writer;
    private RSA serverEncryption, clientEncryption;
    private String nickname;

    MessageListenerOnServer(Socket socket, Set<MessageListenerOnServer> setOfClients, RSA serverEncryption) {
        try {
            setOfClients.add(this);
            this.serverEncryption = serverEncryption;
            this.setOfClients = setOfClients;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Send public key
            writer.println(serverEncryption.e);
            writer.println(serverEncryption.n);
            writer.flush();
            clientEncryption = new RSA(new BigInteger(reader.readLine()), new BigInteger(reader.readLine()));
            this.nickname = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement - Is daemon
        while (true) {
            try {
                String s = reader.readLine();
                if (s == null) {
                    setOfClients.remove(this);
                    break;
                }

                sendToOthers(serverEncryption.decryptString(s));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendToOthers(String s) {
        System.out.println(s);
        for (MessageListenerOnServer i : setOfClients) {
            if (i != this) {
                i.sendMessage(nickname + ": " + s);
            }
        }
    }

    private void sendMessage(String s) {
        writer.println(clientEncryption.encryptString(s));
        writer.flush();
    }
}