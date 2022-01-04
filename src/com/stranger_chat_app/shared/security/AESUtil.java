package com.stranger_chat_app.shared.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {

    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        SecretKey key = generator.generateKey();

        return key;
    }

    public static SecretKey getAESKey(byte[] keyBytes){
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        return secretKeySpec;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static IvParameterSpec getIVParams(byte[] ivBytes){
        IvParameterSpec ivParams = new IvParameterSpec(ivBytes);
        return ivParams;
    }

    public static String encrypt(SecretKey key, String content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParams = AESUtil.generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

        byte[] contentInBytes = BytesUtil.decode(content);
        byte[] encryptedContent = FileUtil.combineBytes(ivParams.getIV(), cipher.doFinal(contentInBytes));

        return Base64.getEncoder().encodeToString(encryptedContent);
    }

    public static String decrypt(SecretKey key, String content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] contentInBytes = Base64.getDecoder().decode(content);
        byte[] ivBytes = Arrays.copyOfRange(contentInBytes, 0, 16);
        contentInBytes = Arrays.copyOfRange(contentInBytes, 16, contentInBytes.length);

        IvParameterSpec ivParams = getIVParams(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);

        return BytesUtil.encode(cipher.doFinal(contentInBytes)).toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String text = "Hello world!";
        System.out.println("Plain text: " + text);

        // Generate key
        SecretKey secretKey = AESUtil.generateAESKey();

        // Encrypt
        String encryptedContent = AESUtil.encrypt(secretKey, text);

        System.out.println("Encrypted: " + encryptedContent);

        // Decrypt
        String decryptedContent = AESUtil.decrypt(secretKey, encryptedContent);

        System.out.println("Decrypted: " + decryptedContent);
    }
}