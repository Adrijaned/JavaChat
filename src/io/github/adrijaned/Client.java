package io.github.adrijaned;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

/**
 * Created by adrijaned on 9.7.17.
 * Main client's class
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 25863);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        RSA encryption = new RSA();
        MessageListenerOnClient messageListener = new MessageListenerOnClient(socket, encryption);
        RSA serverEncryption = new RSA(new BigInteger(messageListener.readRawMessage()), new BigInteger(messageListener.readRawMessage()));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        printWriter.println(encryption.e);
        printWriter.println(encryption.n);
        printWriter.flush();
        do {
            System.out.print("Your new nickname: ");
            printWriter.println(bufferedReader.readLine());
            printWriter.flush();
        } while (!messageListener.readRawMessage().equals(""));
        Thread listener = new Thread(messageListener);
        listener.setDaemon(true);
        listener.start();
        while (true) {
            String s = bufferedReader.readLine();
            if (s.equals("")) {
                break;
            }
            printWriter.println(serverEncryption.encryptString(s));
            printWriter.flush();
        }
    }
}
