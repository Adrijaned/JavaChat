package io.github.adrijaned;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by adrijaned on 10.7.17.
 * RSA encryption handler
 */
class RSA {
    BigInteger d, e, n;
    RSA() {
        BigInteger p, q, lambda;
        p = getRandomPrime();
        q = getRandomPrime();
        n = p.multiply(q);
        lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
        d = findCoprime(lambda);
        e = d.modInverse(lambda);
    }
    RSA(BigInteger e, BigInteger n){
        this.e = e;
        this.n = n;
        this.d = BigInteger.ONE;
    }

    BigInteger findCoprime(BigInteger lambda) {
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

    String encryptString(String message) {
        return new BigInteger(message.getBytes()).modPow(e, n).toString();
    }

    private BigInteger decrypt(BigInteger message) {
        return message.modPow(d, n);
    }

    String decryptString(String message) {
        return new String(decrypt(new BigInteger(message)).toByteArray());
    }
}
