package basic.mvc.utility;

import basic.mvc.utility.exception.CryptoProcessFailedException;
import basic.mvc.utility.exception.SessionTokenExpiredException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CryptoUtilsTest {

    private final String str = UUID.randomUUID().toString().replace("-", "");

    private final byte[] src = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);

    private final String xorKey = "4933910847463829232312312";

    private final String aesKey = "NyS4HAR3zYlS1BzQ077l9gSPU";

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
    public void aesCrypt() {
        byte[] cipher = CryptoUtils.aesEncrypt(str, aesKey);
        String plain = CryptoUtils.aesDecrypt(cipher, aesKey);
        assertEquals(str, plain);
    }

    @Test(expected = CryptoProcessFailedException.class)
    public void aesDecrypt() {
        String plain = CryptoUtils.aesDecrypt("Cipher".getBytes(StandardCharsets.UTF_8), aesKey);
        assertNotEquals(str, plain);
    }

    @Test
    public void tokenCrypt0() {
        String token = CryptoUtils.tokenGenerate(str, aesKey, xorKey);
        String info = CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
        assertEquals(str, info);
    }

    @Test(expected = SessionTokenExpiredException.class)
    public void tokenCrypt1() {
        String content = str + ":" + System.currentTimeMillis();
        byte[] cipher = CryptoUtils.aesEncrypt(content, aesKey);
        String cipher64 = CryptoUtils.base64Encode(cipher).replace("+", "_");
        String token = CryptoUtils.encrypt(cipher64, xorKey);
        CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypt2() {
        CryptoUtils.tokenGenerate("", aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypt3() {
        CryptoUtils.tokenAnalyse("", aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypt4() {
        String content = str + "#" + (System.currentTimeMillis() + (3 * 60 * 60 * 1000));
        byte[] cipher = CryptoUtils.aesEncrypt(content, aesKey);
        String cipher64 = CryptoUtils.base64Encode(cipher).replace("+", "_");
        String token = CryptoUtils.encrypt(cipher64, xorKey);
        CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
    }

}
