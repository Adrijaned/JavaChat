package io.github.adrijaned;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        while (true) {
            String s = bufferedReader.readLine();
            if (s.equals("")) {
                break;
            }
            printWriter.println(s);
            printWriter.flush();
        }
    }
}
