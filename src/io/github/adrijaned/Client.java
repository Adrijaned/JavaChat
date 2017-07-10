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
        printWriter.println("JOINED");
        printWriter.flush();
        Runnable runnable = new MessageListenerOnClient(socket);
        Thread listener = new Thread(runnable);
        listener.setDaemon(true);
        listener.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        RSA encryption = new RSA();
        RSA serverEncryption = new RSA(new BigInteger(bufferedReader.readLine()), new BigInteger(bufferedReader.readLine()));
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
