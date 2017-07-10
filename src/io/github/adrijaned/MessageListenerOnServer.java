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
    private Set<MessageListenerOnServer> set;
    private BufferedReader reader;
    private PrintWriter writer;
    private RSA encryption, clientEncryption;

    MessageListenerOnServer(Socket socket, Set<MessageListenerOnServer> set, RSA encryption) {
        try {
            set.add(this);
            this.encryption = encryption;
            this.set = set;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Send public key
            writer.println(encryption.e);
            writer.println(encryption.n);
            writer.flush();
            clientEncryption = new RSA(new BigInteger(reader.readLine()), new BigInteger(reader.readLine()));
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
                    set.remove(this);
                    break;
                }

                sendToOthers(encryption.decryptString(s));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendToOthers(String s) {
        System.out.println(s);
        for (MessageListenerOnServer i : set) {
            if (i != this){
                i.sendMessage(s);
            }
        }
    }

    private void sendMessage(String s) {
        writer.println(clientEncryption.encryptString(s));
        writer.flush();
    }
}
