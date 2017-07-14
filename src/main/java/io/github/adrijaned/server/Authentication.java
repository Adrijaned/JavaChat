package io.github.adrijaned.server;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adrijaned on 11.7.17.
 * Handles user authentication
 */
class Authentication {
    private final HashMap<String, CredentialsHolder> logins = new HashMap<>();
    private final String passwordFile;

    Authentication(String passwordFile) {
        this.passwordFile = passwordFile;
        loadLogins();
    }

    boolean authenticateUser(String username, String password) {
        return isRegistered(username) && hashString(logins.get(username).getSalt() + password).equals(logins.get(username).getHash());
    }

    boolean registerUser(String username, String password) {
        if (!username.matches("\\w+")) {
            return false;
        }
        logins.put(username, new CredentialsHolder(password));
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(passwordFile, true)));
            printWriter.println(username + ":" + logins.get(username).getSalt() + ":" + logins.get(username).getHash());
            printWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    boolean isRegistered(String username) {
        return logins.containsKey(username);
    }

    void changePassword(String username, String newPassword) {
        logins.replace(username, new CredentialsHolder(newPassword));
        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(passwordFile));
            for (String name : logins.keySet()) {
                printWriter.println(name + ":" + logins.get(name).getSalt() + ":" + logins.get(name).getHash());
            }
            printWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadLogins() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(passwordFile)));
            while (true) {
                String temp = bufferedReader.readLine();
                if (temp == null || temp.equals("")) {
                    break;
                }
                Pattern PASS_PATTERN = Pattern.compile("^(\\w+):(\\d+):(.*)$");
                Matcher matcher = PASS_PATTERN.matcher(temp);
                if (!matcher.matches()) {
                    throw new IOException();
                }
                logins.put(matcher.group(1), new CredentialsHolder(matcher.group(2), matcher.group(3)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashString(String string) {
        return Hashing.sha256().hashString(string, Charsets.UTF_8).toString();
    }
}
