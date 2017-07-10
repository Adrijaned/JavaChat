package io.github.adrijaned;

import java.io.*;
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

    MessageListenerOnServer(Socket socket, Set<MessageListenerOnServer> set) {
        try {
            set.add(this);
            this.set = set;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
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

                sendToOthers(s);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendToOthers(String s) {
        for (MessageListenerOnServer i : set) {
            i.sendMessage(s);
        }
    }

    private void sendMessage(String s) {
        writer.println(s);
        writer.flush();
        System.out.println(s);
    }
}
