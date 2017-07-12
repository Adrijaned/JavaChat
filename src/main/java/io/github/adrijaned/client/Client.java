package io.github.adrijaned.client;


import io.github.adrijaned.shared.ConsoleUtil;
import io.github.adrijaned.shared.LoginResponse;
import io.github.adrijaned.shared.RSA;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

/**
 * Created by adrijaned on 9.7.17.
 * Main client's class
 */
public class Client {
    public static void main(String[] args) throws IOException {
        String host = args[0].matches("(^localhost$|^\\d\\d?\\d?\\.{3}\\d\\d?\\d?$)") ? args[0] : "localhost";
        int port = Integer.valueOf(args[1].matches("\\d{4}\\d?") ? args[1] : "35863");
        Socket socket = new Socket(host, port);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        RSA encryption = new RSA();
        MessageListenerOnClient messageListener = new MessageListenerOnClient(socket, encryption);
        RSA serverEncryption = new RSA(new BigInteger(messageListener.readRawMessage()), new BigInteger(messageListener.readRawMessage()));
        ConsoleUtil console = new ConsoleUtil();
        printWriter.println(encryption.e);
        printWriter.println(encryption.n);
        printWriter.flush();
        logIn(printWriter, messageListener, serverEncryption, console);
        Thread listener = new Thread(messageListener);
        listener.setDaemon(true);
        listener.start();
        userInputHandler(printWriter, serverEncryption, console);
    }

    private static void userInputHandler(PrintWriter printWriter, RSA serverEncryption, ConsoleUtil bufferedReader) throws IOException {
        while (true) {
            String s = bufferedReader.readLine();
            if (s.equals("")) {
                continue;
            }
            if (s.toUpperCase().matches("[/:.\\\\]EXIT")) {
                break;
            }
            printWriter.println(serverEncryption.encryptString(s));
            printWriter.flush();
        }
    }

    private static void logIn(PrintWriter printWriter, MessageListenerOnClient messageListener, RSA serverEncryption, ConsoleUtil consoleUtil) throws IOException {
        LoginResponse response = LoginResponse.AWAITING_LOGIN;
        while (response != LoginResponse.LOGIN_ACCEPTED) {
            switch (response) {
                case AWAITING_LOGIN:
                    System.out.println("Please enter your credentials");
                    break;
                case PASSWORD_INVALID:
                    System.out.println("You have entered invalid password");
                    break;
                case USERNAME_ALREADY_PRESENT:
                    System.out.println("Username is already present on server");
                    break;
                case USERNAME_INVALID:
                    System.out.println("Your username contains disallowed characters");
            }
            System.out.print("Your nickname: ");
            String nick = consoleUtil.readLine();
            if (nick.equals("")) {
                continue;
            }
            System.out.print("Your password: ");
            String pass = consoleUtil.readPass();
            printWriter.println(serverEncryption.encryptString(nick));
            printWriter.println(serverEncryption.encryptString(pass));
            printWriter.flush();
            response = LoginResponse.valueOf(messageListener.readRawMessage());
        }
        System.out.println("Logged in");
    }
}
