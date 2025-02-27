package org.example.lab1;

import java.util.Random;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import org.apache.commons.codec.binary.Hex;

public class RandomNumbers {
    public static void main(String[] args) {
        System.out.println("Random numbers using Math.random():");
        System.out.println(Math.random());
        System.out.println(Math.random());
        System.out.println(Math.random());

        // Q2: java.util.Random
        Random rand = new Random();
        System.out.println("Random.nextDouble():");
        System.out.println(rand.nextDouble());
        System.out.println(rand.nextDouble());
        System.out.println(rand.nextDouble());

        // Q2: Seeding java.util.Random
        rand.setSeed(12345L);
        System.out.println("Seeded Random.nextDouble():");
        System.out.println(rand.nextDouble());
        System.out.println(rand.nextDouble());
        System.out.println(rand.nextDouble());

        // Q3: Random integer, double, and range
        System.out.println("Random integer: " + rand.nextInt());
        System.out.println("Random double (0-1): " + rand.nextDouble());
        System.out.println("Random integer (0-100): " + rand.nextInt(100));

        // Q4: SecureRandom
        SecureRandom secRand = new SecureRandom();
        System.out.println("SecureRandom integer (0-1000): " + secRand.nextInt(1000));
        byte[] seed = secRand.generateSeed(20);
        System.out.println("SecureRandom seed (Hex): " + Hex.encodeHexString(seed));

        // Q5: SecureRandom with setSeed
        secRand.setSeed(12345L);
        System.out.println("SecureRandom with fixed seed:");
        System.out.println(secRand.nextInt(100));
        System.out.println(secRand.nextInt(100));
        System.out.println(secRand.nextInt(100));

        // Q6: Linear Congruential Generator
        int seedValue = 5;
        int mod = 7;
        int multiplier = 3;
        int inc = 3;
        int prev = seedValue;

        System.out.println("Linear Congruential Generator output:");
        for (int i = 0; i < 20; i++) {
            int random = (multiplier * prev + inc) % mod;
            prev = random;
            System.out.print(random + " ");
        }
        System.out.println();

        // Q7: SecureRandom Providers
        for (Provider provider : Security.getProviders()) {
            System.out.println("Provider: " + provider.getName());
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                if (service.getType().equals("SecureRandom")) {
                    System.out.println("  SecureRandom Algorithm: " + service.getAlgorithm());
                }
            }
        }
    }
}
