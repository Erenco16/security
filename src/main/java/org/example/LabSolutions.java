package org.example;

import java.security.*;
import java.util.*;
import javax.crypto.*;
import java.io.*;
import java.nio.file.*;
import org.apache.commons.codec.binary.Hex;

public class LabSolutions {
    
    // Lab 1 - Random Numbers
    public static void lab1_randomNumbers() {
        System.out.println("Lab 1 - Random Numbers\n");
        
        // Q1: Math.random()
        System.out.println("Math.random() values:");
        System.out.println(Math.random());
        System.out.println(Math.random());
        System.out.println(Math.random());
        
        // Q2: java.util.Random
        Random rand = new Random();
        System.out.println("Random.nextDouble():");
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
    }
    
    // Lab 2 - Message Digests (SHA-256)
    public static void lab2_messageDigests() throws Exception {
        System.out.println("\nLab 2 - Message Digests\n");
        
        String message = "Hello, Security!";
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(message.getBytes());
        System.out.println("SHA-256 Hash: " + Hex.encodeHexString(hash));
        
        // File Hashing
        Path filePath = Paths.get("data/test.txt");
        byte[] fileBytes = Files.readAllBytes(filePath);
        byte[] fileHash = sha256.digest(fileBytes);
        System.out.println("File SHA-256 Hash (Base64): " + Base64.getEncoder().encodeToString(fileHash));
    }
    
    // Lab 3 - HMAC
    public static void lab3_hmac() throws Exception {
        System.out.println("\nLab 3 - HMAC\n");
        
        // Generate secret key
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keyGen.generateKey();
        
        // Store key to file
        try (ObjectOutputStream keyOut = new ObjectOutputStream(new FileOutputStream("data/secretKey"))) {
            keyOut.writeObject(secretKey);
        }
        
        // Store message to file
        String message = "Secure Message";
        Files.write(Paths.get("data/message.txt"), message.getBytes());
        
        // Compute HMAC
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hmac = mac.doFinal(message.getBytes());
        
        // Write HMAC to file
        Files.write(Paths.get("data/hmac"), hmac);
        
        // Read and verify HMAC
        SecretKey readKey;
        try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream("data/secretKey"))) {
            readKey = (SecretKey) keyIn.readObject();
        }
        byte[] storedHmac = Files.readAllBytes(Paths.get("data/hmac"));
        
        Mac verifyMac = Mac.getInstance("HmacSHA256");
        verifyMac.init(readKey);
        byte[] computedHmac = verifyMac.doFinal(Files.readAllBytes(Paths.get("data/message.txt")));
        
        System.out.println("HMAC verification: " + Arrays.equals(storedHmac, computedHmac));
    }
    
    public static void main(String[] args) throws Exception {
        lab1_randomNumbers();
        lab2_messageDigests();
        lab3_hmac();
    }
}

