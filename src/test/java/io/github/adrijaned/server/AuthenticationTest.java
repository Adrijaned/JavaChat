package io.github.adrijaned.server;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

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
        File tempTestFile = File.createTempFile("userAuth", "txt");
        tempTestFile.deleteOnExit();
        Authentication authentication = new Authentication(tempTestFile.getAbsolutePath());
        authentication.registerUser("re", "re");
        Assert.assertFalse(authentication.authenticateUser("regex", "regexOne"));
        Assert.assertTrue(authentication.registerUser("regex", "regexOne"));
        Assert.assertTrue(authentication.authenticateUser("regex", "regexOne"));
    }

    @Test
    public void testChangePassword() throws Exception {
        File tempTestFile = File.createTempFile("userAuth", "txt");
        tempTestFile.deleteOnExit();
        Authentication authentication = new Authentication(tempTestFile.getAbsolutePath());
        authentication.registerUser("test", "test");
        Assert.assertTrue(authentication.authenticateUser("test", "test"));
        authentication.changePassword("test", "tester");
        Assert.assertFalse(authentication.authenticateUser("test", "test"));
        Assert.assertTrue(authentication.authenticateUser("test", "tester"));
    }
}