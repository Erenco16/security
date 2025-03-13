package org.example.lab5;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Base64;

public class DiffieHellman {
    public static void writeToFile(String filename, Object object) throws Exception {
        File file = new File("./src/main/java/org/example/lab5/data/" + filename); // Ensure file is created in the current directory

        // Ensure the parent directory exists
        file.getParentFile().mkdirs();

        try (FileOutputStream fout = new FileOutputStream(file);
             ObjectOutputStream oout = new ObjectOutputStream(fout)) {
            oout.writeObject(object);
        }
    }
    public static Object readFromFile(String filename) throws Exception {
        FileInputStream fin = new FileInputStream("./src/main/java/org/example/lab5/data/" + filename);
        ObjectInputStream oin = new ObjectInputStream(fin);
        Object object = oin.readObject();
        oin.close();
        return object;
    }

    public class Q1{
        public static void generateParams() throws Exception {
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);
            // Generate the parameters
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = params.getParameterSpec(DHParameterSpec.class);
            String s = dhSpec.getP() + "," + dhSpec.getG() + "," + dhSpec.getL();
            System.out.println(s);
            DiffieHellman.writeToFile("dhParams", s) ;
        }
    }

    public class Q2{
        public static void saveDHKeys(String PARTY) throws Exception {
            // get DH parameters
             String valuesInStr = (String) DiffieHellman.readFromFile("dhParams");
             String[] values = valuesInStr.split(",");
             BigInteger p = new BigInteger(values[0]);
             BigInteger g = new BigInteger(values[1]);
             int l = Integer.parseInt(values[2]);
            // Create an instance of DH with the parameters
             DHParameterSpec dhSpec = new DHParameterSpec(p, g, l);
            // Use the values to generate a key pair
             KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
             keyGen.initialize(dhSpec);
             KeyPair keypair = keyGen.generateKeyPair();
            // Save the private key
            PrivateKey privateKey = keypair.getPrivate();
            writeToFile(  PARTY + "Private", privateKey) ;
            // Save the public key
             PublicKey publicKey = keypair.getPublic();
             writeToFile( PARTY + "Public", publicKey) ;
             System.out.println("DH Keys saved for " + PARTY);
        }
    }

    public class Q3{
        public static void generateAESKeyAndCheck() throws Exception {
            // read both keypairs i.e. 4 keys
            PrivateKey privateKey1 = (PrivateKey) readFromFile("AlicePrivate");
            PrivateKey privateKey2 = (PrivateKey) readFromFile("BobPrivate");
            PublicKey publicKey1 = (PublicKey) readFromFile("AlicePublic");
            PublicKey publicKey2 = (PublicKey) readFromFile("BobPublic");
            // AlicePrivate and BobPublic
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(privateKey1);
            ka.doPhase(publicKey2, true);
            byte[] rawValue = ka.generateSecret();
            SecretKey secretKey1 = new SecretKeySpec(rawValue, 0, 16, "AES");
            String encodedKey = Base64.getEncoder().encodeToString(secretKey1.getEncoded());
            System.out.println("Base64 encoded secret key 1 " + encodedKey);
            // AlicePublic and BobPrivate
            ka.init(privateKey2);
            ka.doPhase(publicKey1, true);
            byte[] rawValue2 = ka.generateSecret();
            SecretKey secretKey2 = new SecretKeySpec(rawValue2, 0, 16, "AES");
            String encodedKey2 = Base64.getEncoder().encodeToString(secretKey2.getEncoded());
            System.out.println("Base64 encoded secret key 2 " + encodedKey2);
            if (!encodedKey.equals(encodedKey2)) {
                System.out.println("Base64 encoded secret keys are not same");
            }
            System.out.println("Base64 encoded secret keys are same");
        }
    }

    public static void main(String[] args) throws Exception {
//        Q1.generateParams();
//        String PARTY = args[0];
//        Q2.saveDHKeys(PARTY);
        Q3.generateAESKeyAndCheck();
    }
}
