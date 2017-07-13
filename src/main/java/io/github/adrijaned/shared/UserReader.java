package io.github.adrijaned.shared;

import java.io.IOException;

public interface UserReader {
    String readLine() throws IOException;

    String readPass() throws IOException;
}
