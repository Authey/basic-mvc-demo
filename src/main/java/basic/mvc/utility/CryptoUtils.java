package basic.mvc.utility;

import basic.mvc.utility.exception.SessionTokenExpiredException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CryptoUtils {

    private static final Cipher engine;

    static {
        try {
            engine = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64Encode(byte[] src) {
        return Base64Utils.encodeToString(src);
    }

    public static byte[] base64Decode(String src) {
        return Base64Utils.decodeFromString(src);
    }

    // Plain -> Byte -> PlainInt -> CipherInt -> Cipher
    public static String encrypt(String plain, String xorKey) {
        if (StringUtils.isBlank(plain)) {
            return "";
        }
        BigInteger plainInt = new BigInteger(plain.getBytes(StandardCharsets.UTF_8));
        BigInteger key = new BigInteger(xorKey);
        BigInteger cipherInt = plainInt.xor(key);
        return cipherInt.toString(16);
    }

    // Cipher -> CipherInt -> PlainInt -> Byte -> Plain
    public static String decrypt(String cipher, String xorKey) {
        if (StringUtils.isBlank(cipher)) {
            return "";
        }
        BigInteger cipherInt = new BigInteger(cipher, 16);
        BigInteger key = new BigInteger(xorKey);
        BigInteger plainInt = cipherInt.xor(key);
        return new String(plainInt.toByteArray(), StandardCharsets.UTF_8);
    }

    private static SecretKeySpec aesInit(String aesKey) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(aesKey.getBytes(StandardCharsets.UTF_8));
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128, random);
        return new SecretKeySpec(keyGen.generateKey().getEncoded(), "AES");
    }

    public static byte[] aesEncrypt(String plain, String aesKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        engine.init(Cipher.ENCRYPT_MODE, aesInit(aesKey));
        return engine.doFinal(plain.getBytes(StandardCharsets.UTF_8));
    }

    public static String aesDecrypt(byte[] cipher, String aesKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        engine.init(Cipher.DECRYPT_MODE, aesInit(aesKey));
        return new String(engine.doFinal(cipher), StandardCharsets.UTF_8);
    }

    // Info -> Content -> AES Cipher -> Base64 Cipher -> XOR Cipher
    public static String tokenGenerate(String info, String aesKey, String xorKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        assert StringUtils.isNotBlank(info);
        String content = info + ":" + (System.currentTimeMillis() + (3 * 60 * 60 * 1000));
        byte[] cipher = aesEncrypt(content, aesKey);
        String cipher64 = base64Encode(cipher).replace("+", "_");
        return encrypt(cipher64, xorKey);
    }

    // XOR Cipher -> Base64 Cipher -> AES Cipher -> Content -> Info
    public static String tokenAnalyse(String token, String aesKey, String xorKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        assert StringUtils.isNotBlank(token);
        String cipher64 = decrypt(token, xorKey);
        byte[] cipher = base64Decode(cipher64.replace("_", "+"));
        String content = aesDecrypt(cipher, aesKey);
        assert content.contains(":");
        String[] infoList = content.split(":");
        if (System.currentTimeMillis() > Long.parseLong(infoList[1])) {
            throw new SessionTokenExpiredException("Session Token [" + token + "] Has Already Expired");
        }
        return infoList[0];
    }

}
