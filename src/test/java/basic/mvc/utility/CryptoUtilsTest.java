package basic.mvc.utility;

import basic.mvc.utility.exception.CryptoProcessFailedException;
import basic.mvc.utility.exception.SessionTokenExpiredException;
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
        assertEquals(new String(Base64Utils.encode(src)), res);
    }

    @Test
    public void base64Decode() {
        String base64Str = Base64Utils.encodeToString(src);
        byte[] res = CryptoUtils.base64Decode(base64Str);
        assertArrayEquals(src, res);
    }

    @Test
    public void encrypt0() {
        String res = CryptoUtils.encrypt(str, xorKey);
        BigInteger plain = new BigInteger(str.getBytes(StandardCharsets.UTF_8));
        BigInteger key = new BigInteger(xorKey);
        BigInteger cipher = plain.xor(key);
        assertEquals(cipher.toString(16), res);
    }

    @Test
    public void encrypt1() {
        String res = CryptoUtils.encrypt("", xorKey);
        assertEquals("", res);
    }

    @Test
    public void decrypt0() {
        BigInteger plain = new BigInteger(str.getBytes(StandardCharsets.UTF_8));
        BigInteger key = new BigInteger(xorKey);
        BigInteger cipher = plain.xor(key);
        String res = CryptoUtils.decrypt(cipher.toString(16), xorKey);
        assertEquals(str, res);
    }

    @Test
    public void decrypt1() {
        String res = CryptoUtils.decrypt("", xorKey);
        assertEquals("", res);
    }

    @Test
    public void hash() {
        byte[] cipher = CryptoUtils.hash("Plain");
        assertEquals(32, cipher.length);
    }

    @Test
    public void secretKeyGenerate() {
        String res = CryptoUtils.secretKeyGenerate(uid);
        assertEquals(aesKey, res);
        assertEquals(44, res.length()); // 32 + 12 = 44
        String dum = "MyS4HAR3zYlS1BzQ077l9gSPO";
        assertNotEquals(uid, dum);
        String fake = CryptoUtils.secretKeyGenerate(dum);
        assertNotEquals(aesKey, fake);
        assertEquals(44, fake.length()); // 32 + 12 = 44
    }

    @Test
    public void aesCrypto() {
        byte[] cipher = CryptoUtils.aesEncrypt(str, aesKey);
        String plain = CryptoUtils.aesDecrypt(cipher, aesKey);
        assertEquals(str, plain);
    }

    @Test
    public void aesEncrypt() {
        byte[] cipher = CryptoUtils.aesEncrypt("Plain", aesKey);
        assertEquals(16, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void aesDecrypt() {
        String plain = CryptoUtils.aesDecrypt("Cipher".getBytes(StandardCharsets.UTF_8), aesKey);
        assertNotEquals(str, plain);
    }

    @Test
    public void keyPairGenerate() {
        Map<String, String> res = CryptoUtils.keyPairGenerate(uid);
        assertEquals(rsaKey, res);
        assertEquals(392, res.get("pubKey").length());
        assertEquals(1624, res.get("priKey").length());
        String dum = "MyS4HAR3zYlS1BzQ077l9gSPO";
        assertNotEquals(uid, dum);
        Map<String, String> fake = CryptoUtils.keyPairGenerate(dum);
        assertNotEquals(rsaKey, fake);
        assertEquals(392, fake.get("pubKey").length());
        assertEquals(1624, fake.get("priKey").length());
    }

    @Test
    public void rsaCrypto() {
        byte[] cipher = CryptoUtils.rsaPublicEncrypt(str, rsaKey.get("pubKey"));
        String plain = CryptoUtils.rsaPrivateDecrypt(cipher, rsaKey.get("priKey"));
        assertEquals(str, plain);
        String encoded = CryptoUtils.base64Encode(cipher);
        byte[] hashed = CryptoUtils.hash(encoded);
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
    public void rsaPublicEncrypt() {
        byte[] cipher = CryptoUtils.rsaPublicEncrypt("Plain", rsaKey.get("pubKey"));
        assertEquals(256, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPrivateDecrypt() {
        String plain = CryptoUtils.rsaPrivateDecrypt("Cipher".getBytes(StandardCharsets.UTF_8), rsaKey.get("priKey"));
        assertNotEquals("Cipher", plain);
    }

    @Test
    public void rsaPrivateEncrypt() {
        byte[] cipher = CryptoUtils.rsaPrivateEncrypt("Plain".getBytes(StandardCharsets.UTF_8), rsaKey.get("priKey"));
        assertEquals(256, cipher.length);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaPublicDecrypt() {
        byte[] plain = CryptoUtils.rsaPublicDecrypt("Cipher".getBytes(StandardCharsets.UTF_8), rsaKey.get("pubKey"));
        assertNotEquals("Cipher", new String(plain, StandardCharsets.UTF_8));
    }

    @Test
    public void rsaSign() {
        byte[] sign = CryptoUtils.rsaSign("Data".getBytes(StandardCharsets.UTF_8), rsaKey.get("priKey"));
        assertEquals(256, sign.length);
    }

    @Test
    public void rsaVerify0() {
        byte[] signFalse = new byte[256];
        Random random = new Random();
        random.nextBytes(signFalse);
        assertEquals(256, signFalse.length);
        byte[] signTrue = CryptoUtils.rsaSign("Data".getBytes(StandardCharsets.UTF_8), rsaKey.get("priKey"));
        assertNotEquals(signTrue, signFalse);
        boolean verifyFalse = CryptoUtils.rsaVerify("Data".getBytes(StandardCharsets.UTF_8), signFalse, rsaKey.get("pubKey"));
        assertFalse(verifyFalse);
        boolean verifyTrue = CryptoUtils.rsaVerify("Data".getBytes(StandardCharsets.UTF_8), signTrue, rsaKey.get("pubKey"));
        assertTrue(verifyTrue);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void rsaVerify1() {
        boolean verify = CryptoUtils.rsaVerify("Data".getBytes(StandardCharsets.UTF_8), "Sign".getBytes(StandardCharsets.UTF_8), rsaKey.get("pubKey"));
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
        String token = CryptoUtils.encrypt(cipher64, xorKey);
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
        String token = CryptoUtils.encrypt(cipher64, xorKey);
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
        byte[] res = CryptoUtils.hash(str);
        String hex = CryptoUtils.toHex(res);
        assertEquals(2 * res.length, hex.length());
    }

    @Test
    public void toHex2() {
        byte[] res = CryptoUtils.aesEncrypt(str, aesKey);
        String hex = CryptoUtils.toHex(res);
        assertEquals(2 * res.length, hex.length());
    }

    @Test
    public void toHex3() {
        byte[] res = CryptoUtils.rsaPublicEncrypt(str, rsaKey.get("pubKey"));
        String hex = CryptoUtils.toHex(res);
        assertEquals(2 * res.length, hex.length());
    }

}
