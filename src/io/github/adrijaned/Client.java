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
        MessageListenerToClient messageListener = new MessageListenerToClient(socket, encryption);
        Thread listener = new Thread(messageListener);
        listener.setDaemon(true);
        RSA serverEncryption = new RSA(new BigInteger(messageListener.readRawMessage()), new BigInteger(messageListener.readRawMessage()));
        listener.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        printWriter.println(encryption.e);
        printWriter.println(encryption.n);
        printWriter.flush();
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
