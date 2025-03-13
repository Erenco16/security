package org.example.serverLab5;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DHServerSkel {

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(2000);
        while (true) {
            System.out.println("Server: waiting for connection ..");

            // Accept connection
            Socket s = ss.accept();
            System.out.println("Client connected!");
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            // Read DH parameters from client
            String dhParams = (String) ois.readObject();
            System.out.println("Received DH Params: " + dhParams);

            // Create DHParameterSpec
            String[] values = dhParams.split(",");
            BigInteger pp = new BigInteger(values[0]);
            BigInteger g = new BigInteger(values[1]);
            int l = Integer.parseInt(values[2]);
            DHParameterSpec dhParameterSpec = new DHParameterSpec(pp, g, l);

            // ✅ Send DH parameters to client
            oos.writeObject(dhParameterSpec);
            oos.flush();
            System.out.println("Sent DH parameters to client.");

            // ✅ Generate own DH key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            keyGen.initialize(dhParameterSpec);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // ✅ Receive client's public key (Base64 encoded)
            String clientEncodedPublicKey = (String) ois.readObject();
            System.out.println("Received Base64-encoded client public key: " + clientEncodedPublicKey);

            // ✅ Decode client's public key
            byte[] decodedClientKey = Base64.getDecoder().decode(clientEncodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(decodedClientKey);
            PublicKey clientPublicKey = keyFactory.generatePublic(x509KeySpec);
            System.out.println("Decoded client’s public key.");

            // ✅ Send own public key (Base64 encoded)
            String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            oos.writeObject(encodedPublicKey);
            oos.flush();
            System.out.println("Sent Base64-encoded public key to client.");

            // ✅ Generate symmetric key
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(clientPublicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();

            // ✅ Generate AES key from shared secret
            SecretKey secretKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

            // ✅ Base64 encode and print the symmetric key
            String encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println("Generated Base64-encoded symmetric key: " + encodedSecretKey);

            // Close the connection
            oos.close();
            ois.close();
            s.close();
            System.out.println("Connection closed.");
        }
    }
}
