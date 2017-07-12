package io.github.adrijaned.shared;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by adrijaned on 10.7.17.
 * RSA encryption handler
 */
public class RSA {
    public BigInteger e, n;
    private BigInteger d;

    public RSA() {
        BigInteger p, q, lambda;
        p = getRandomPrime();
        q = getRandomPrime();
        n = p.multiply(q);
        lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
        d = findCoprime(lambda);
        e = d.modInverse(lambda);
    }

    public RSA(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
        this.d = BigInteger.ONE;
    }

    private BigInteger findCoprime(BigInteger lambda) {
        BigInteger d = new BigInteger("2");
        while (!lambda.gcd(d).equals(BigInteger.ONE)) {
            d = d.add(BigInteger.ONE);
        }
        return d;
    }

    private BigInteger getRandomPrime() {
        return new BigInteger(1024, 200, new Random());
    }

    private BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b).divide(a.gcd(b));
    }

    public String encryptString(String message) {
        if (message == null) {
            return null;
        }
        return new BigInteger(message.getBytes()).modPow(e, n).toString();
    }

    private BigInteger decrypt(BigInteger message) {
        if (message == null) {
            return null;
        }
        return message.modPow(d, n);
    }

    public String decryptString(String message) {
        if (message == null) {
            return null;
        }
        return new String(decrypt(new BigInteger(message)).toByteArray());
    }
}
