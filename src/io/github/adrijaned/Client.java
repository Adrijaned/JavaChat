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
        String rawMessage = "Please enter your credentials";
        while (!rawMessage.equals("")) {
            System.out.println(rawMessage);
            System.out.print("Your nickname: ");
            String nick = bufferedReader.readLine();
            if (nick.equals("")) {
                continue;
            }
            System.out.print("Your password: ");
            String pass = bufferedReader.readLine();
            if (pass.equals("")) {
                System.out.println("Connected");
                continue;
            }
            printWriter.println(serverEncryption.encryptString(nick));
            printWriter.println(serverEncryption.encryptString(pass));
            printWriter.flush();
            rawMessage = messageListener.readRawMessage();
        }
        Thread listener = new Thread(messageListener);
        listener.setDaemon(true);
        listener.start();
        while (true) {
            String s = bufferedReader.readLine();
            if (s.equals("")) {
                continue;
            }
            if (s.toUpperCase().matches("[/:.\\\\]exit")) {
                break;
            }
            printWriter.println(serverEncryption.encryptString(s));
            printWriter.flush();
        }
    }
}
