package io.github.adrijaned.server;

import org.junit.Assert;
import org.junit.Test;

public class AuthenticationTest {
    @Test
    public void testIsRegistered() throws Exception {
        Authentication authentication = new Authentication("src/test/java/userAuth.txt");
        Assert.assertTrue(authentication.isRegistered("asdf"));
    }

    @Test
    public void testIsNotRegistered() throws Exception {
        Authentication authentication = new Authentication("src/test/java/userAuth.txt");
        Assert.assertFalse(authentication.isRegistered("GHJK"));
    }

    @Test
    public void testIsAuthenticated() throws Exception {
        Authentication authentication = new Authentication("src/test/java/userAuth.txt");
        Assert.assertTrue(authentication.authenticateUser("asdf", "GHJK"));
    }

    @Test
    public void testIsNotAuthenticated() throws Exception {
        Authentication authentication = new Authentication("src/test/java/userAuth.txt");
        Assert.assertFalse(authentication.authenticateUser("asdf", "ghjk"));
    }

    @Test
    public void testRegistration() throws Exception {
        Authentication authentication = new Authentication("src/test/java/userAuth.txt");
        Assert.assertFalse(authentication.isRegistered("regex"));
        Assert.assertTrue(authentication.registerUser("regex", "regexOne"));
        Assert.assertTrue(authentication.isRegistered("regex"));
    }
}