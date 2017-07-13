package io.github.adrijaned.shared;

import java.io.Console;

/**
 * Created by adrijaned on 13.7.17.
 */
public class ConsoleUserReader implements UserReader {
    private final Console console = System.console();

    @Override
    public String readLine() {
        return console.readLine();
    }

    @Override
    public String readPass() {
        return new String(console.readPassword());
    }
}
