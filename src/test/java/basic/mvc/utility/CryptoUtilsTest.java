package basic.mvc.utility;

import basic.mvc.utility.exception.CryptoProcessFailedException;
import basic.mvc.utility.exception.SessionTokenExpiredException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.util.Base64Utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CryptoUtilsTest {

    private final String str = UUID.randomUUID().toString().replace("-", "");

    private final byte[] src = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);

    private final String uid = "NyS4HAR3zYlS1BzQ077l9gSPU";

    private final String xorKey = "4933910847463829232312312";

    private final String aesKey = CryptoUtils.secretKeyGenerate(uid);

    private final Map<String, String> rsaKey = CryptoUtils.keyPairGenerate(uid);

    @Test
    public void base64Encode() {
        String res = CryptoUtils.base64Encode(src);
        assertEquals(Base64Utils.encodeToString(src), res);
        assertEquals(new String(Base64Utils.encode(src), StandardCharsets.UTF_8), res);
    }

    @Test
    public void base64Decode() {
        byte[] res = CryptoUtils.base64Decode(str);
        assertArrayEquals(Base64Utils.decodeFromString(str), res);
        assertArrayEquals(Base64Utils.decode(str.getBytes(StandardCharsets.UTF_8)), res);
    }

    @Test
    public void crypto() {
        String cipher = CryptoUtils.xorEncrypt(str, xorKey);
        String plain = CryptoUtils.xorDecrypt(cipher, xorKey);
        assertEquals(str, plain);
    }

    @Test
    public void xorEncrypt0() {
        String res = CryptoUtils.xorEncrypt(str, xorKey);
        BigInteger plain = new BigInteger(str.getBytes(StandardCharsets.UTF_8));
        BigInteger key = new BigInteger(xorKey);
        BigInteger cipher = plain.xor(key);
        assertEquals(cipher.toString(16), res);
    }

    @Test
    public void xorEncrypt1() {
        String cipherTrue = CryptoUtils.xorEncrypt(str, xorKey);
        int keyLength = xorKey.length();
        String key = RandomStringUtils.randomNumeric(keyLength);
        assertEquals(keyLength, key.length());
        String cipherFalse = CryptoUtils.xorEncrypt(str, key);
        assertNotEquals(cipherTrue, cipherFalse);
    }

    @Test
    public void xorEncrypt2() {
        String res = CryptoUtils.xorEncrypt("", xorKey);
        assertEquals("", res);
    }

    @Test
    public void xorDecrypt0() {
        BigInteger plain = new BigInteger(str.getBytes(StandardCharsets.UTF_8));
        BigInteger key = new BigInteger(xorKey);
        BigInteger cipher = plain.xor(key);
        String res = CryptoUtils.xorDecrypt(cipher.toString(16), xorKey);
        assertEquals(str, res);
    }

    @Test
    public void xorDecrypt1() {
        String cipher = CryptoUtils.xorEncrypt(str, xorKey);
        int keyLength = xorKey.length();
        String key = RandomStringUtils.randomNumeric(keyLength);
        assertEquals(keyLength, key.length());
        String plainFalse = CryptoUtils.xorDecrypt(cipher, key);
        assertNotEquals(str, plainFalse);
    }

    @Test
    public void xorDecrypt2() {
        String res = CryptoUtils.xorDecrypt("", xorKey);
        assertEquals("", res);
    }

    @Test
    public void hash0() {
        byte[] cipher = CryptoUtils.hash(str, "MD5");
        assertEquals(16, cipher.length);
    }

    @Test
    public void hash1() {
        byte[] cipher = CryptoUtils.hash(str, "SHA1");
        assertEquals(20, cipher.length);
    }

    @Test
    public void hash2() {
        byte[] cipher = CryptoUtils.hash(str, "SHA-256");
        assertEquals(32, cipher.length);
    }

    @Test
    public void hash3() {
        byte[] cipher = CryptoUtils.hash(str, "SHA-512");
        assertEquals(64, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void hash4() {
        byte[] cipher = CryptoUtils.hash(str, "Random");
        assertNotEquals(16, cipher.length);
    }

    @Test
    public void secretKeyGenerate() {
        String res = CryptoUtils.secretKeyGenerate(uid);
        assertEquals(aesKey, res);
        String dum = "MyS4HAR3zYlS1BzQ077l9gSPO";
        assertNotEquals(uid, dum);
        String fake = CryptoUtils.secretKeyGenerate(dum);
        assertNotEquals(aesKey, fake);
    }

    @Test
    public void aesCrypto() {
        byte[] cipher = CryptoUtils.aesEncrypt(str, aesKey);
        String plain = CryptoUtils.aesDecrypt(cipher, aesKey);
        assertEquals(str, plain);
    }

    @Test
    public void aesEncrypt0() {
        byte[] cipher = CryptoUtils.aesEncrypt(str, aesKey);
        assertEquals(((str.length() / 16) + 1) * 16, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void aesEncrypt1() {
        int keyLength = aesKey.length();
        String secretKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, secretKey.length());
        byte[] cipher = CryptoUtils.aesEncrypt(str, secretKey);
        assertEquals(((str.length() / 16) + 1) * 16, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void aesDecrypt0() {
        byte[] cipher = CryptoUtils.aesEncrypt(str, aesKey);
        int keyLength = aesKey.length();
        String secretKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, secretKey.length());
        String plain = CryptoUtils.aesDecrypt(cipher, secretKey);
        assertNotEquals(str, plain);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void aesDecrypt1() {
        String plain = CryptoUtils.aesDecrypt(str.getBytes(StandardCharsets.UTF_8), aesKey);
        assertNotEquals(str, plain);
    }

    @Test
    public void keyPairGenerate() {
        Map<String, String> res = CryptoUtils.keyPairGenerate(uid);
        assertEquals(rsaKey, res);
        String dum = "MyS4HAR3zYlS1BzQ077l9gSPO";
        assertNotEquals(uid, dum);
        Map<String, String> fake = CryptoUtils.keyPairGenerate(dum);
        assertNotEquals(rsaKey, fake);
    }

    @Test
    public void rsaCrypto() {
        byte[] cipher = CryptoUtils.rsaPublicEncrypt(str, rsaKey.get("pubKey"));
        String plain = CryptoUtils.rsaPrivateDecrypt(cipher, rsaKey.get("priKey"));
        assertEquals(str, plain);
        String encoded = CryptoUtils.base64Encode(cipher);
        byte[] hashed = CryptoUtils.hash(encoded, "SHA-256");
        assertTrue(hashed.length <= 256 - 11);
        byte[] cipherHash = CryptoUtils.rsaPrivateEncrypt(hashed, rsaKey.get("priKey"));
        byte[] plainHash = CryptoUtils.rsaPublicDecrypt(cipherHash, rsaKey.get("pubKey"));
        assertArrayEquals(hashed, plainHash);
        byte[] cipherFur = CryptoUtils.rsaPrivateEncrypt(src, rsaKey.get("priKey"));
        byte[] plainFur = CryptoUtils.rsaPublicDecrypt(cipherFur, rsaKey.get("pubKey"));
        assertArrayEquals(src, plainFur);
        byte[] sign = CryptoUtils.rsaSign(cipher, rsaKey.get("priKey"));
        boolean verify = CryptoUtils.rsaVerify(cipher, sign, rsaKey.get("pubKey"));
        assertTrue(verify);
    }

    @Test
    public void rsaPublicEncrypt0() {
        byte[] cipher = CryptoUtils.rsaPublicEncrypt(str, rsaKey.get("pubKey"));
        assertEquals(256, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPublicEncrypt1() {
        int keyLength = rsaKey.get("pubKey").length();
        String pubKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, pubKey.length());
        byte[] cipher = CryptoUtils.rsaPublicEncrypt(str, pubKey);
        assertEquals(256, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPrivateDecrypt0() {
        byte[] cipher = CryptoUtils.rsaPublicEncrypt(str, rsaKey.get("pubKey"));
        int keyLength = rsaKey.get("priKey").length();
        String priKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, priKey.length());
        String plain = CryptoUtils.rsaPrivateDecrypt(cipher, priKey);
        assertNotEquals(str, plain);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPrivateDecrypt1() {
        String plain = CryptoUtils.rsaPrivateDecrypt(str.getBytes(StandardCharsets.UTF_8), rsaKey.get("priKey"));
        assertNotEquals(str, plain);
    }

    @Test
    public void rsaPrivateEncrypt0() {
        byte[] cipher = CryptoUtils.rsaPrivateEncrypt(src, rsaKey.get("priKey"));
        assertEquals(256, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPrivateEncrypt1() {
        int keyLength = rsaKey.get("priKey").length();
        String priKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, priKey.length());
        byte[] cipher = CryptoUtils.rsaPrivateEncrypt(src, priKey);
        assertEquals(256, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPublicDecrypt0() {
        byte[] cipher = CryptoUtils.rsaPrivateEncrypt(str.getBytes(StandardCharsets.UTF_8), rsaKey.get("priKey"));
        int keyLength = rsaKey.get("pubKey").length();
        String pubKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, pubKey.length());
        byte[] plain = CryptoUtils.rsaPublicDecrypt(cipher, pubKey);
        assertNotEquals(str, new String(plain, StandardCharsets.UTF_8));
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPublicDecrypt1() {
        byte[] plain = CryptoUtils.rsaPublicDecrypt(str.getBytes(StandardCharsets.UTF_8), rsaKey.get("pubKey"));
        assertNotEquals(str, new String(plain, StandardCharsets.UTF_8));
    }

    @Test
    public void rsaSign0() {
        byte[] sign = CryptoUtils.rsaSign(src, rsaKey.get("priKey"));
        assertEquals(256, sign.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaSign1() {
        int keyLength = rsaKey.get("priKey").length();
        String priKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, priKey.length());
        byte[] sign = CryptoUtils.rsaSign(src, priKey);
        assertEquals(256, sign.length);
    }

    @Test
    public void rsaVerify0() {
        byte[] signFalse = new byte[256];
        Random random = new Random();
        random.nextBytes(signFalse);
        assertEquals(256, signFalse.length);
        byte[] signTrue = CryptoUtils.rsaSign(src, rsaKey.get("priKey"));
        assertNotEquals(signTrue, signFalse);
        boolean verifyFalse = CryptoUtils.rsaVerify(src, signFalse, rsaKey.get("pubKey"));
        assertFalse(verifyFalse);
        boolean verifyTrue = CryptoUtils.rsaVerify(src, signTrue, rsaKey.get("pubKey"));
        assertTrue(verifyTrue);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaVerify1() {
        byte[] sign = CryptoUtils.rsaSign(src, rsaKey.get("priKey"));
        int keyLength = rsaKey.get("pubKey").length();
        String pubKey = RandomStringUtils.randomAlphabetic(keyLength);
        assertEquals(keyLength, pubKey.length());
        boolean verify = CryptoUtils.rsaVerify(src, sign, pubKey);
        assertFalse(verify);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaVerify2() {
        boolean verify = CryptoUtils.rsaVerify(src, src, rsaKey.get("pubKey"));
        assertFalse(verify);
    }

    @Test
    public void tokenCrypto0() {
        String token = CryptoUtils.tokenGenerate(str, aesKey, xorKey);
        String info = CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
        assertEquals(str, info);
    }

    @Test
    public void tokenCrypto1() {
        String token = CryptoUtils.tokenGenerate(str, aesKey, xorKey);
        assertEquals(128, token.length());
        String info = CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
        assertEquals(str.length(), info.length());
    }

    @Test(expected = SessionTokenExpiredException.class)
    public void tokenCrypto2() {
        String content = str + ":" + (System.currentTimeMillis() - (60 * 60 * 1000));
        byte[] cipher = CryptoUtils.aesEncrypt(content, aesKey);
        String cipher64 = CryptoUtils.base64Encode(cipher).replace("+", "_");
        String token = CryptoUtils.xorEncrypt(cipher64, xorKey);
        CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypto3() {
        CryptoUtils.tokenGenerate("", aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypto4() {
        CryptoUtils.tokenAnalyse("", aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypto5() {
        String content = str + "#" + (System.currentTimeMillis() + (3 * 60 * 60 * 1000));
        byte[] cipher = CryptoUtils.aesEncrypt(content, aesKey);
        String cipher64 = CryptoUtils.base64Encode(cipher).replace("+", "_");
        String token = CryptoUtils.xorEncrypt(cipher64, xorKey);
        CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
    }

    @Test
    public void toHex0() {
        byte[] src = new byte[]{-128, 127, -127, 59, -64, 64, -97, 36, -39, 89};
        String res = CryptoUtils.toHex(src);
        assertEquals("807F813BC0409F24D959", res.toUpperCase());
    }

    @Test
    public void toHex1() {
        byte[] res = CryptoUtils.hash(str, "SHA-256");
        String hex = CryptoUtils.toHex(res);
        assertEquals(64, hex.length());
    }

    @Test
    public void toHex2() {
        byte[] res = CryptoUtils.aesEncrypt(str, aesKey);
        String hex = CryptoUtils.toHex(res);
        assertEquals(((str.length() / 16) + 1) * 32, hex.length());
    }

    @Test
    public void toHex3() {
        byte[] res = CryptoUtils.rsaPublicEncrypt(str, rsaKey.get("pubKey"));
        String hex = CryptoUtils.toHex(res);
        assertEquals(512, hex.length());
    }

}
