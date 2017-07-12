package io.github.adrijaned.shared;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by adrijaned on 12.7.17.
 * Coverage for System.console()
 */
public class ConsoleUtil {
    private Console c;
    private BufferedReader bufferedReader;

    public ConsoleUtil() {
        c = System.console();
        if (c == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
    }

    public String readLine() throws IOException {
        if (c == null) {
            return bufferedReader.readLine();
        }
        return c.readLine();
    }

    public String readPass() throws IOException {
        if (c == null) {
            return readLine();
        }
        return new String(c.readPassword());
    }
}
