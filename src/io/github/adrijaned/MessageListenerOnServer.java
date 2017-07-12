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
    private static final Pattern CMD_PATTERN = Pattern.compile("^[/:.\\\\] ?(\\w+) .*$");
    private Map<String, MessageListenerOnServer> mapOfClients;
    private BufferedReader reader;
    private PrintWriter writer;
    private RSA serverEncryption, clientEncryption;
    private String username;

    MessageListenerOnServer(Socket socket, Map<String, MessageListenerOnServer> mapOfClients, RSA serverEncryption, Authentication authenticator) {
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
            username = logUserIn(mapOfClients, serverEncryption, authenticator);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String logUserIn(Map<String, MessageListenerOnServer> mapOfClients, RSA serverEncryption, Authentication authenticator) throws IOException {
        String username = serverEncryption.decryptString(reader.readLine());
        String pass = serverEncryption.decryptString(reader.readLine());
        while (!authenticator.authenticateUser(username, pass) || mapOfClients.containsKey(username)) {
            writer.println(mapOfClients.containsKey(username) ? "Username is already present on server" : "Invalid credentials");
            writer.flush();
            username = serverEncryption.decryptString(reader.readLine());
            pass = serverEncryption.decryptString(reader.readLine());
        }
        mapOfClients.put(username, this);
        writer.println("Logged in.");
        writer.flush();
        return username;
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement - Is daemon
        while (true) {
            try {
                String s = reader.readLine();
                if (s == null) {
                    mapOfClients.remove(username);
                    break;
                }
                String message = serverEncryption.decryptString(s);
                Matcher matcher = CMD_PATTERN.matcher(message);
                if (matcher.matches()) {
                    switch (matcher.group(1).toUpperCase()) {
                        case "CLIENTS":
                            sendMessage("List of connected users:");
                            sendMessage(String.join("\n", mapOfClients.keySet()));
                            break;
                    }
                } else {
                    Matcher msgMatcher = MSG_PATTERN.matcher(message);
                    if (msgMatcher.matches()) {
                        System.out.println(username + " @ " + msgMatcher.group(1) + " :  " + msgMatcher.group(2));
                        mapOfClients.get(msgMatcher.group(1)).sendMessage(username + " @ you" + " :  " + msgMatcher.group(2));
                    } else {
                        sendToOthers(message);
                    }
                }
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
                i.sendMessage(username + ": " + s);
            }
        }
    }

    private void sendMessage(String s) {
        writer.println(clientEncryption.encryptString(s));
        writer.flush();
    }
}
