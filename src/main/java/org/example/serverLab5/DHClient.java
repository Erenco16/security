package org.example.serverLab5;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DHClient {

    public static void main(String[] args) throws Exception {

        System.out.println("Client");

        // Socket
        InetAddress inet = InetAddress.getByName("localhost");
        Socket s = new Socket(inet, 2000);

        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

        // Send DH parameters as string
        String params = generateParams();
        System.out.println("Params " + params);
        oos.writeObject(params);
        oos.flush();

        // ✅ Read DH parameters as String and reconstruct DHParameterSpec
        String receivedParams = (String) ois.readObject();
        String[] values = receivedParams.split(",");
        BigInteger pp = new BigInteger(values[0]);
        BigInteger g = new BigInteger(values[1]);
        int l = Integer.parseInt(values[2]);
        DHParameterSpec dhParameterSpec = new DHParameterSpec(pp, g, l);
        System.out.println("Received DH parameters from server.");

        // ✅ Generate DH key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
        keyGen.initialize(dhParameterSpec);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // ✅ Send own public key (Base64 encoded)
        String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        oos.writeObject(encodedPublicKey);
        oos.flush();
        System.out.println("Sent Base64-encoded public key to server.");

        // ✅ Receive server’s public key (Base64 encoded)
        String serverEncodedPublicKey = (String) ois.readObject();
        System.out.println("Received Base64-encoded server public key: " + serverEncodedPublicKey);

        // ✅ Decode and reconstruct server's PublicKey object
        byte[] decodedServerKey = Base64.getDecoder().decode(serverEncodedPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(decodedServerKey);
        PublicKey serverPublicKey = keyFactory.generatePublic(x509KeySpec);
        System.out.println("Decoded server's public key.");

        // ✅ Generate symmetric key
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(privateKey);
        ka.doPhase(serverPublicKey, true);
        byte[] sharedSecret = ka.generateSecret();

        // ✅ Generate AES key from shared secret
        SecretKey secretKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

        // ✅ Base64 encode and print the symmetric key
        String encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Base64-encoded symmetric key: " + encodedSecretKey);

        // Close the connection
        oos.close();
        ois.close();
        s.close();
    }

    public static String generateParams() {
        try {
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = params.getParameterSpec(DHParameterSpec.class);
            return dhSpec.getP() + "," + dhSpec.getG() + "," + dhSpec.getL();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
