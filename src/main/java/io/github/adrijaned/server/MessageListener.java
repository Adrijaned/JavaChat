package io.github.adrijaned.server;

import io.github.adrijaned.shared.LoginResponse;
import io.github.adrijaned.shared.RSA;

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

class MessageListener {
    private static final Pattern MSG_PATTERN = Pattern.compile("^@ ?(\\w+) (.*)$");
    private final Map<String, MessageListener> mapOfClients;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final RSA serverEncryption, clientEncryption;
    private final String username;
    private final Authentication authenticator;
    private final Socket socket;

    MessageListener(Socket socket, Map<String, MessageListener> mapOfClients, RSA serverEncryption, Authentication authenticator) throws IOException {
        this.socket = socket;
        this.authenticator = authenticator;
        this.serverEncryption = serverEncryption;
        this.mapOfClients = mapOfClients;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        // Send public key
        writer.println(serverEncryption.e);
        writer.println(serverEncryption.n);
        writer.flush();
        clientEncryption = new RSA(new BigInteger(reader.readLine()), new BigInteger(reader.readLine()));
        username = logUserIn();
    }

    private String logUserIn() throws IOException {
        String username;
        do {
            username = serverEncryption.decryptString(reader.readLine());
            String pass = serverEncryption.decryptString(reader.readLine());
            if (mapOfClients.containsKey(username)) {
                writer.println(LoginResponse.USERNAME_ALREADY_PRESENT.name());
                writer.flush();
            } else if (authenticator.isRegistered(username)) {
                if (authenticator.authenticateUser(username, pass)) {
                    break;
                }
                writer.println(LoginResponse.PASSWORD_INVALID.name());
                writer.flush();
            } else if (authenticator.registerUser(username, pass)) {
                break;
            } else {
                writer.println(LoginResponse.USERNAME_INVALID.name());
                writer.flush();
            }

        } while (true);
        mapOfClients.put(username, this);
        writer.println(LoginResponse.LOGIN_ACCEPTED.name());
        writer.flush();
        broadcast("User " + username + " logged in.");
        return username;
    }

    void listen() {
        while (true) {
            try {
                String s = reader.readLine();
                if (s == null) {
                    removeUser();
                    break;
                }
                String message = serverEncryption.decryptString(s);
                if (message.toUpperCase().startsWith("/CLIENTS")) {
                    sendMessage("List of connected users:");
                    sendMessage(String.join("\n", mapOfClients.keySet()));
                } else if (message.toUpperCase().startsWith("/CHPASS")) {
                    String pass;
                    String[] chPassString = message.split(" ");
                    if (chPassString.length > 1) {
                        pass = chPassString[1];
                    } else {
                        continue;
                    }
                    authenticator.changePassword(username, pass);
                    sendMessage("Password changed");
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
                removeUser();
                break;
            }
        }
    }

    private void removeUser() {
        broadcast("User " + username + " left.");
        mapOfClients.remove(username);
    }

    void kickUser() {
        broadcast("User " + username + " was kicked.");
        sendMessage("You were kicked.");
        mapOfClients.remove(username);
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToOthers(String s) {
        broadcast(username + " :  " + s);
    }

    private void broadcast(String s) {
        System.out.println(s);
        for (MessageListener i : mapOfClients.values()) {
            if (i != this) {
                i.sendMessage(s);
            }
        }

    }

    void sendMessage(String s) {
        writer.println(clientEncryption.encryptString(s));
        writer.flush();
    }
}
