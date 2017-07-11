package io.github.adrijaned;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adrijaned on 11.7.17.
 * Handles user authentication
 */
class Authentication {
    private HashMap<String, String> logins = new HashMap<>();

    Authentication(String passwordFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(passwordFile)));
            while (true) {
                String temp = bufferedReader.readLine();
                if (temp == null) {
                    break;
                }
                Pattern PASS_PATTERN = Pattern.compile("^(\\w+):(.*)$");
                Matcher matcher = PASS_PATTERN.matcher(temp);
                if (!matcher.matches()) {
                    throw new IOException();
                }
                logins.put(matcher.group(1), matcher.group(2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean authenticateUser(String username, String password) {
        return password.equals(logins.get(username));
    }
}
