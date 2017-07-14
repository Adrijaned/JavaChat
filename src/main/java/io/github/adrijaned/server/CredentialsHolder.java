package io.github.adrijaned.server;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.util.Random;

class CredentialsHolder {
    private final String salt;
    private final String hash;

    CredentialsHolder(String salt, String hash) {
        this.salt = salt;
        this.hash = hash;
    }

    CredentialsHolder(String password) {
        this.salt = Long.toString(Math.abs(new Random().nextLong()));
        this.hash = Hashing.sha256().hashString(this.salt + password, Charsets.UTF_8).toString();
    }

    String getSalt() {
        return salt;
    }

    String getHash() {
        return hash;
    }
}
