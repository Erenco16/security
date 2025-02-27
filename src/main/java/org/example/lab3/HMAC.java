package org.example.lab3;

import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import org.apache.commons.codec.binary.Hex;

public class HMAC
{
    public static void lab3_hmac() throws Exception {
        System.out.println("\nLab 3 - HMAC\n");

        // Q1: HMAC-SHA256 Signature
        String message = "Hello, HMAC!";
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keyGen.generateKey();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hmac = mac.doFinal(message.getBytes());
        System.out.println("HMAC-SHA256 Signature: " + Hex.encodeHexString(hmac));

        // Q2: Store Key, Message, and HMAC to File
        writeToFile("data/secretKey", secretKey);
        Files.write(Paths.get("data/message.txt"), message.getBytes());
        Files.write(Paths.get("data/hmac"), hmac);

        // Q3: Verify HMAC
        SecretKey readKey = (SecretKey) readFromFile("data/secretKey");
        byte[] storedHmac = Files.readAllBytes(Paths.get("data/hmac"));
        Mac verifyMac = Mac.getInstance("HmacSHA256");
        verifyMac.init(readKey);
        byte[] computedHmac = verifyMac.doFinal(Files.readAllBytes(Paths.get("data/message.txt")));
        System.out.println("HMAC verification: " + Arrays.equals(storedHmac, computedHmac));
    }

    public static void writeToFile(String filename, Object object) throws Exception {
        FileOutputStream fout = new FileOutputStream(filename);
        ObjectOutputStream oout = new ObjectOutputStream(fout);
        oout.writeObject(object);
        oout.close();
    }

    public static Object readFromFile(String filename) throws Exception {
        FileInputStream fin = new FileInputStream(filename);
        ObjectInputStream oin = new ObjectInputStream(fin);
        Object object = oin.readObject();
        oin.close();
        return object;
    }

    public static void main(String[] args) throws Exception {
        try {
        lab3_hmac();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
