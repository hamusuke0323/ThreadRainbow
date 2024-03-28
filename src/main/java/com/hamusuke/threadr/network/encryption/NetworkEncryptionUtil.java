package com.hamusuke.threadr.network.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class NetworkEncryptionUtil {
    private static final String AES = "AES";
    private static final int AES_KEY_LENGTH = 128;
    private static final String RSA = "RSA";
    private static final int RSA_KEY_LENGTH = 1024;

    public static SecretKey generateKey() throws Exception {
        var keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(AES_KEY_LENGTH);
        return keyGenerator.generateKey();
    }

    public static KeyPair generateServerKeyPair() throws Exception {
        var keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(RSA_KEY_LENGTH);
        return keyPairGenerator.generateKeyPair();
    }

    public static PublicKey readEncodedPublicKey(byte[] bytes) throws Exception {
        var encodedKeySpec = new X509EncodedKeySpec(bytes);
        var keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(encodedKeySpec);
    }

    public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) throws Exception {
        var bs = decrypt(privateKey, encryptedSecretKey);
        return new SecretKeySpec(bs, AES);
    }

    public static byte[] encrypt(Key key, byte[] data) throws Exception {
        return crypt(1, key, data);
    }

    public static byte[] decrypt(Key key, byte[] data) throws Exception {
        return crypt(2, key, data);
    }

    private static byte[] crypt(int opMode, Key key, byte[] data) throws Exception {
        return crypt(opMode, key.getAlgorithm(), key).doFinal(data);
    }

    private static Cipher crypt(int opMode, String algorithm, Key key) throws Exception {
        var cipher = Cipher.getInstance(algorithm);
        cipher.init(opMode, key);
        return cipher;
    }

    public static Cipher cipherFromKey(int opMode, Key key) throws Exception {
        var cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
        return cipher;
    }
}
