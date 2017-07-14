package io.github.adrijaned.server;

import io.github.adrijaned.shared.ConsoleUserReader;
import io.github.adrijaned.shared.SystemInUserReader;
import io.github.adrijaned.shared.UserReader;

import java.io.IOException;
import java.util.Map;

public class ServerConsole implements Runnable {
    private Map<String, MessageListener> clients;

    ServerConsole(Map<String, MessageListener> map) {
        clients = map;
    }

    @Override
    public void run() {
        UserReader reader = System.console() == null ? new SystemInUserReader() : new ConsoleUserReader();
        while (true) {
            System.out.print("Command: ");
            try {
                String s = reader.readLine();
                if (s.toUpperCase().startsWith("BROADCAST ")) {
                    broadcastMessage(s.substring("BROADCAST ".length()));
                } else if (s.toUpperCase().startsWith("CLIENTS")) {
                    printClients();
                } else if (s.toUpperCase().startsWith("KICK ")) {
                    kickUser(s.substring("KICK ".length()));
                } else {
                    System.out.println("--!UNKNOWN COMMAND!--");
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void printClients() {
        System.out.println(String.join("\n", clients.keySet()));
    }

    private void broadcastMessage(String line) {
        for (MessageListener client : clients.values()) {
            client.sendMessage(line);
        }
    }

    private void kickUser(String username) throws IOException {
        if (clients.keySet().contains(username)) {
            clients.get(username).kickUser();
        } else {
            System.out.println("User not found");
        }
    }
}

