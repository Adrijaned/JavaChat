package io.github.adrijaned.shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemInUserReader implements UserReader {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public String readPass() throws IOException {
        return readLine();
    }
}
