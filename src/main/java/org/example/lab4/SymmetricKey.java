package org.example.lab4;

import org.apache.commons.codec.binary.Hex;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import java.util.Base64;

public class SymmetricKey {
    public static class Q1 {
        private byte[] iv; // Store IV for reuse

        public String encryptString(String plaintext, SecretKey key, String ALGORITHM) throws Exception {
            Cipher eCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Use padding
            SecureRandom random = new SecureRandom();
            iv = new byte[16]; // Store IV for reuse
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            eCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            System.out.println("Clear text: " + plaintext);

            byte[] ciphertext = eCipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(ciphertext); // Use Base64 instead of Hex
        }

        public String decryptString(String ciphertext, SecretKey key, String ALGORITHM) throws Exception {
            Cipher dCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Use same padding

            // Ensure we use the **same IV** from encryption
            IvParameterSpec ivSpec = new IvParameterSpec(iv); // Reuse IV
            dCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] cipherBytes = Base64.getDecoder().decode(ciphertext); // Decode Base64
            byte[] plainTextBytes = dCipher.doFinal(cipherBytes);

            return new String(plainTextBytes);
        }
    }
    public static class Q2 {
        public static class Employee implements Serializable {
            private static final long serialVersionUID = 1L;
            String name;
            String address;
            String telNo;

            public Employee(String name, String address, String telNo) {
                this.name = name;
                this.address = address;
                this.telNo = telNo;
            }

            @Override
            public String toString() {
                return "Employee{" +
                        "name='" + name + '\'' +
                        ", address='" + address + '\'' +
                        ", telNo='" + telNo + '\'' +
                        '}';
            }
        }
    }

    public static class Q3 {
        private static void writeToFile(String filename, Object object) throws Exception {
            File file = new File("./src/main/java/org/example/lab4/" + filename); // Ensure file is created in the current directory

            // Ensure the parent directory exists
            file.getParentFile().mkdirs();

            try (FileOutputStream fout = new FileOutputStream(file);
                 ObjectOutputStream oout = new ObjectOutputStream(fout)) {
                oout.writeObject(object);
            }
        }
        static Object readFromFile(String filename) throws Exception {
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream oin = new ObjectInputStream(fin);
            Object object = oin.readObject();
            oin.close();
            return object;
        }
    }

    public static void main(String[] args) throws Exception {
        Q1 q1 = new Q1();
        String ALGORITHM = "AES";

        // Generate the key
        KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
        keygen.init(128);
        SecretKey key = keygen.generateKey();

        String text = "Hello World";

        // Encrypt
        String encryptedText = q1.encryptString(text, key, ALGORITHM);
        System.out.println("Encryption: \n" + encryptedText);

        // Decrypt
        String decryptedText = q1.decryptString(encryptedText, key, ALGORITHM);
        System.out.println("Decryption: \n" + decryptedText);

        // Create an Employee object
        Q2.Employee employee = new Q2.Employee("John Doe", "123 Main St", "555-1234");
        System.out.println("Original Employee Object: " + employee);

        // Create Cipher instance for encryption
        Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // Encrypt Employee object inside SealedObject
        SealedObject sealedObject = new SealedObject(employee, encryptCipher);
        System.out.println("Employee object sealed successfully.");

        // this part is for the question 3,
        // we are writing the sealedObject and secretKet to another file
        try{
            Q3.writeToFile("data/sealedObject.dat", sealedObject);
            Q3.writeToFile("data/secretKey.txt", key);
        }catch (Exception e){
            e.printStackTrace();
        }
        // Create Cipher instance for decryption
        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, key, ivSpec); // Reuse IV

        // Extract the object from SealedObject
        Q2.Employee decryptedEmployee = (Q2.Employee) sealedObject.getObject(decryptCipher);

        // Print the decrypted Employee object
        System.out.println("Decrypted Employee Object: " + decryptedEmployee);

        // second part of the question 3 is below
        // reading the key and the sealedobject and then
        // extracting and reading these objects

        String keyFile = "src/main/java/org/example/lab4/data/secretKey.txt";
        String sealedObjectFile = "src/main/java/org/example/lab4/data/sealedObject.dat";

        // Read the secret key from file
        SecretKey secretKey = (SecretKey) Q3.readFromFile(keyFile);

        // Read the SealedObject from file
        SealedObject sealedObject2 = (SealedObject) Q3.readFromFile(sealedObjectFile);

        // Create Cipher instance for decryption
        Cipher decryptCipher2 = Cipher.getInstance("AES");
        decryptCipher2.init(Cipher.DECRYPT_MODE, secretKey);

        // Extract Employee object from SealedObject
        Q2.Employee employee2 = (Q2.Employee) sealedObject2.getObject(decryptCipher);

        // Print Employee object contents
        System.out.println("Decrypted Employee Object: " + employee2);
    }
}
