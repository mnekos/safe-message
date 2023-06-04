package pl.mnekos.safemessage;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESUtils {
    public static SecretKey generateSecretKeyFromString(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    public static String encryptAES(String message, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedBase64Bytes = Base64.getEncoder().encode(encryptedBytes);

        return new String(encryptedBase64Bytes, StandardCharsets.UTF_8);
    }

    public static String decryptAES(String encryptedMessage, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Długość klucza AES: 128, 192 lub 256 bitów
        return keyGenerator.generateKey();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    public static String toString(SecretKey key) {
        byte[] rawData = key.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    public static boolean isValidKey(String key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");
            return secretKey.getEncoded().length == 16 || secretKey.getEncoded().length == 24 || secretKey.getEncoded().length == 32;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
