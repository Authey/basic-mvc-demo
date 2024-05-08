package basic.mvc.utility;

import basic.mvc.utility.exception.CryptoProcessFailedException;
import basic.mvc.utility.exception.SessionTokenExpiredException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public final class CryptoUtils {

    public static String base64Encode(byte[] src) {
        return Base64Utils.encodeToString(src);
    }

    public static byte[] base64Decode(String src) {
        return Base64Utils.decodeFromString(src);
    }

    // Plain -> Byte -> PlainInt -> CipherInt -> Cipher
    public static String xorEncrypt(String plain, String xorKey) {
        if (StringUtils.isBlank(plain)) {
            return "";
        }
        BigInteger plainInt = new BigInteger(plain.getBytes(StandardCharsets.UTF_8));
        BigInteger key = new BigInteger(xorKey);
        BigInteger cipherInt = plainInt.xor(key);
        return cipherInt.toString(16);
    }

    // Cipher -> CipherInt -> PlainInt -> Byte -> Plain
    public static String xorDecrypt(String cipher, String xorKey) {
        if (StringUtils.isBlank(cipher)) {
            return "";
        }
        BigInteger cipherInt = new BigInteger(cipher, 16);
        BigInteger key = new BigInteger(xorKey);
        BigInteger plainInt = cipherInt.xor(key);
        return new String(plainInt.toByteArray(), StandardCharsets.UTF_8);
    }

    public static byte[] hash(String plain, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(plain.getBytes(StandardCharsets.UTF_8));
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoProcessFailedException("Perform Hash Encoding Failed: ", e);
        }
    }

    private static Cipher cipherInit(String instance) {
        try {
            return Cipher.getInstance(instance);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new CryptoProcessFailedException("Initial Cipher Engine Failed: ", e);
        }
    }

    public static String secretKeyGenerate(String uid) {
        try {
            SecureRandom random = new SecureRandom();
            random.setSeed(uid.getBytes(StandardCharsets.UTF_8));
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, random);
            byte[] secretKey = keyGen.generateKey().getEncoded();
            return base64Encode(secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoProcessFailedException("Generate AES Secret Key Failed: ", e);
        }
    }

    private static SecretKeySpec secretKeyConvert(String aesKey) {
        byte[] decodedKey = base64Decode(aesKey);
        return new SecretKeySpec(decodedKey, "AES");
    }

    public static byte[] aesEncrypt(String plain, String aesKey) {
        try {
            Cipher engine = cipherInit("AES");
            engine.init(Cipher.ENCRYPT_MODE, secretKeyConvert(aesKey));
            return engine.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoProcessFailedException("Perform AES Encryption Failed: ", e);
        }
    }

    public static String aesDecrypt(byte[] cipher, String aesKey) {
        try {
            Cipher engine = cipherInit("AES");
            engine.init(Cipher.DECRYPT_MODE, secretKeyConvert(aesKey));
            return new String(engine.doFinal(cipher), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoProcessFailedException("Perform AES Decryption Failed: ", e);
        }
    }

    private static Signature signatureInit() {
        try {
            return Signature.getInstance("SHA256withRSA");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoProcessFailedException("Initial Signature Engine Failed: ", e);
        }
    }

    public static Map<String, String> keyPairGenerate(String uid) {
        try {
            SecureRandom random = new SecureRandom();
            random.setSeed(uid.getBytes(StandardCharsets.UTF_8));
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            byte[] pubKey = keyPair.getPublic().getEncoded();
            byte[] priKey = keyPair.getPrivate().getEncoded();
            Map<String, String> keyPairMap = new HashMap<>();
            keyPairMap.put("pubKey", base64Encode(pubKey));
            keyPairMap.put("priKey", base64Encode(priKey));
            return keyPairMap;
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoProcessFailedException("Generate RSA Key Pair Failed: ", e);
        }
    }

    // X509 Standard For Public Key
    private static PublicKey publicKeyConvert(String pubKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = base64Decode(pubKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptoProcessFailedException("Acquire RSA Public Key Failed: ", e);
        }
    }

    // PKCS8 Standard For Private Key
    private static PrivateKey privateKeyConvert(String priKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = base64Decode(priKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptoProcessFailedException("Acquire RSA Private Key Failed: ", e);
        }
    }

    // Send - Encrypt Using Public Key of Oppo
    public static byte[] rsaPublicEncrypt(String plain, String pubKey) {
        try {
            Cipher engine = cipherInit("RSA");
            engine.init(Cipher.ENCRYPT_MODE, publicKeyConvert(pubKey));
            return engine.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoProcessFailedException("Perform RSA Encryption Failed: ", e);
        }
    }

    // Receive - Decrypt Using Private Key of Self
    public static String rsaPrivateDecrypt(byte[] cipher, String priKey) {
        try {
            Cipher engine = cipherInit("RSA");
            engine.init(Cipher.DECRYPT_MODE, privateKeyConvert(priKey));
            return new String(engine.doFinal(cipher), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoProcessFailedException("Perform RSA Decryption Failed: ", e);
        }
    }

    // Send - Encrypt Using Private Key of Self
    public static byte[] rsaPrivateEncrypt(byte[] plain, String priKey) {
        try {
            Cipher engine = cipherInit("RSA");
            engine.init(Cipher.ENCRYPT_MODE, privateKeyConvert(priKey));
            return engine.doFinal(plain);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoProcessFailedException("Perform RSA Encryption Failed: ", e);
        }
    }

    // Receive - Decrypt Using Public Key of Oppo
    public static byte[] rsaPublicDecrypt(byte[] cipher, String pubKey) {
        try {
            Cipher engine = cipherInit("RSA");
            engine.init(Cipher.DECRYPT_MODE, publicKeyConvert(pubKey));
            return engine.doFinal(cipher);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoProcessFailedException("Perform RSA Decryption Failed: ", e);
        }
    }

    // Send - Sign Using Private Key of Self
    public static byte[] rsaSign(byte[] data, String priKey) {
        try {
            Signature engine = signatureInit();
            engine.initSign(privateKeyConvert(priKey));
            engine.update(data);
            return engine.sign();
        } catch (InvalidKeyException | SignatureException e) {
            throw new CryptoProcessFailedException("Perform RSA Signature Failed: ", e);
        }
    }

    // Receive - Verify Using Public Key of Oppo
    public static boolean rsaVerify(byte[] data, byte[] sign, String pubKey) {
        try {
            Signature engine = signatureInit();
            engine.initVerify(publicKeyConvert(pubKey));
            engine.update(data);
            return engine.verify(sign);
        } catch (InvalidKeyException | SignatureException e){
            throw new CryptoProcessFailedException("Perform RSA Verification Failed: ", e);
        }
    }

    // Info -> Content -> AES Cipher -> Base64 Cipher -> XOR Cipher
    public static String tokenGenerate(String info, String aesKey, String xorKey) {
        assert StringUtils.isNotBlank(info);
        String content = info + ":" + (System.currentTimeMillis() + (3 * 60 * 60 * 1000));
        byte[] cipher = aesEncrypt(content, aesKey);
        String cipher64 = base64Encode(cipher).replace("+", "_");
        return xorEncrypt(cipher64, xorKey);
    }

    // XOR Cipher -> Base64 Cipher -> AES Cipher -> Content -> Info
    public static String tokenAnalyse(String token, String aesKey, String xorKey) {
        assert StringUtils.isNotBlank(token);
        String cipher64 = xorDecrypt(token, xorKey);
        byte[] cipher = base64Decode(cipher64.replace("_", "+"));
        String content = aesDecrypt(cipher, aesKey);
        assert content.contains(":");
        String[] infoList = content.split(":");
        if (System.currentTimeMillis() > Long.parseLong(infoList[1])) {
            throw new SessionTokenExpiredException("Session Token [" + token + "] Has Already Expired");
        }
        return infoList[0];
    }

    public static String toHex(byte[] cipher) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : cipher) {
            String curHex = Integer.toHexString(b & 0xFF);
            hexStr.append(curHex.length() == 1 ? "0" : "");
            hexStr.append(curHex);
        }
        return hexStr.toString();
    }

}
