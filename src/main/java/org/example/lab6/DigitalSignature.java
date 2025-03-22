package org.example.lab6;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DigitalSignature{

    // Q1: Digitally sign some text and verify it
    public static class Q1 {
        public static void run() throws Exception {
            System.out.println("\nRunning Q1: Sign and Verify Text\n");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            String message = "This is the text to sign";
            Signature signer = Signature.getInstance("SHA256withDSA");

            // Sign the message
            signer.initSign(privateKey);
            signer.update(message.getBytes());
            byte[] signature = signer.sign();
            System.out.println("Signature created.");

            // Verify the message
            signer.initVerify(publicKey);
            signer.update(message.getBytes());
            boolean verifies = signer.verify(signature);
            System.out.println("Signature verifies: " + verifies);
        }
    }

    // Q2: Sign, write text, key, and signature to files & then read and verify
    public static class Q2_Write {
        public static void run() throws Exception {
            System.out.println("\nRunning Q2: Write text, key, and signature to files\n");

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            String message = "This is the text to sign and write";

            Signature signer = Signature.getInstance("SHA256withDSA");
            signer.initSign(privateKey);
            signer.update(message.getBytes());
            byte[] signature = signer.sign();

            Files.write(new File("./src/main/java/org/example/lab6/data/message.txt").toPath(), message.getBytes());
            Files.write(new File("./src/main/java/org/example/lab6/data/publickey.dat").toPath(), publicKey.getEncoded());
            Files.write(new File("./src/main/java/org/example/lab6/data/signature.dat").toPath(), signature);

            System.out.println("Message, public key, and signature written to files.");
        }
    }

    public static class Q2_ReadVerify {
        public static void run() throws Exception {
            System.out.println("\nRunning Q2: Read from files and verify\n");

            byte[] messageBytes = Files.readAllBytes(new File("./src/main/java/org/example/lab6/data/message.txt").toPath());
            byte[] publicKeyBytes = Files.readAllBytes(new File("./src/main/java/org/example/lab6/data/publickey.dat").toPath());
            byte[] signatureBytes = Files.readAllBytes(new File("./src/main/java/org/example/lab6/data/signature.dat").toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            Signature signer = Signature.getInstance("SHA256withDSA");
            signer.initVerify(publicKey);
            signer.update(messageBytes);
            boolean verifies = signer.verify(signatureBytes);

            System.out.println("Signature from file verifies: " + verifies);
        }
    }

    public static void main(String[] args) throws Exception {
        Q1.run();
        Q2_Write.run();
        Q2_ReadVerify.run();
    }
}
