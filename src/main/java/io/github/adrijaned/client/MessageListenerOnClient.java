package io.github.adrijaned.client;

import io.github.adrijaned.shared.RSA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageListenerOnClient implements Runnable {
    private BufferedReader bufferedReader;
    private RSA encryption;

    MessageListenerOnClient(Socket socket, RSA encryption) {
        try {
            this.encryption = encryption;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String line = encryption.decryptString(bufferedReader.readLine());
                if (line == null) {
                    System.out.println("Connection lost.");
                    System.exit(0);
                }
                System.out.println(line);
            } catch (IOException e) {
                break;
            }
        }
    }

    String readRawMessage() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
