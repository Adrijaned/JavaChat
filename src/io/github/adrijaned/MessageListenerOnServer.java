package io.github.adrijaned;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adrijaned on 9.7.17.
 * On server, listen on client's socket and forward all incoming messages to server.
 */

public class MessageListenerOnServer implements Runnable {
    private static final Pattern MSG_PATTERN = Pattern.compile("^@ ?(\\w+) (.*)$");
    private Map<String, MessageListenerOnServer> mapOfClients;
    private BufferedReader reader;
    private PrintWriter writer;
    private RSA serverEncryption, clientEncryption;
    private String nickname;

    MessageListenerOnServer(Socket socket, Map<String, MessageListenerOnServer> mapOfClients, RSA serverEncryption) {
        try {
            this.serverEncryption = serverEncryption;
            this.mapOfClients = mapOfClients;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Send public key
            writer.println(serverEncryption.e);
            writer.println(serverEncryption.n);
            writer.flush();
            clientEncryption = new RSA(new BigInteger(reader.readLine()), new BigInteger(reader.readLine()));
            this.nickname = reader.readLine();
            while (mapOfClients.containsKey(nickname) || !nickname.matches("^\\w+$")) {
                writer.println("Error");
                writer.flush();
                this.nickname = reader.readLine();
            }
            mapOfClients.put(nickname, this);
            writer.println("");
            writer.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement - Is daemon
        while (true) {
            try {
                boolean isKeyword = false;
                String s = reader.readLine();
                if (s == null) {
                    mapOfClients.remove(nickname);
                    break;
                }
                String message = serverEncryption.decryptString(s);
                if (message.startsWith("/")) {
                    switch (message.split(" ")[0].toUpperCase()) {
                        case "/CLIENTS":
                            sendMessage("List of connected users:");
                            sendMessage(String.join("\n", mapOfClients.keySet()));
                            isKeyword = true;
                            break;
                    }
                } else {
                    Matcher msgMatcher = MSG_PATTERN.matcher(message);
                    if (msgMatcher.matches()) {
                        isKeyword = true;
                        System.out.println(nickname + " @ " + msgMatcher.group(1) + " :  " + msgMatcher.group(2));
                        mapOfClients.get(msgMatcher.group(1)).sendMessage(nickname + " @ you" + " :  " + msgMatcher.group(2));
                    }
                }
                if (isKeyword) {
                    continue;
                }
                sendToOthers(message);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendToOthers(String s) {
        System.out.println(s);
        for (MessageListenerOnServer i : mapOfClients.values()) {
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
