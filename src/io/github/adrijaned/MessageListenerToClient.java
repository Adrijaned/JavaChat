package io.github.adrijaned;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by adrijaned on 9.7.17.
 * On client, listen to messages and print them
 */
public class MessageListenerToClient implements Runnable{
    private BufferedReader bufferedReader;
    RSA encryption;
    MessageListenerToClient(Socket socket, RSA encryption)  {
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
        while (true){
            try {
                String line = encryption.decryptString(bufferedReader.readLine());
                if (line == null){
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