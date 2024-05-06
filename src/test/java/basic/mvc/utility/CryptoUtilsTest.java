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
    public void hash() {
        byte[] cipher = CryptoUtils.hash("Plain");
        assertEquals(32, cipher.length);
    }

    @Test
    public void aesCrypt() {
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
    public void tokenCrypt0() {
        String token = CryptoUtils.tokenGenerate(str, aesKey, xorKey);
        String info = CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
        assertEquals(str, info);
    }

    @Test
    public void tokenCrypt1() {
        String token = CryptoUtils.tokenGenerate(str, aesKey, xorKey);
        assertEquals(128, token.length());
        String info = CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
        assertEquals(str.length(), info.length());
    }

    @Test(expected = SessionTokenExpiredException.class)
    public void tokenCrypt2() {
        String content = str + ":" + (System.currentTimeMillis() - (60 * 60 * 1000));
        byte[] cipher = CryptoUtils.aesEncrypt(content, aesKey);
        String cipher64 = CryptoUtils.base64Encode(cipher).replace("+", "_");
        String token = CryptoUtils.encrypt(cipher64, xorKey);
        CryptoUtils.tokenAnalyse(token, aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypt3() {
        CryptoUtils.tokenGenerate("", aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypt4() {
        CryptoUtils.tokenAnalyse("", aesKey, xorKey);
    }

    @Test(expected = AssertionError.class)
    public void tokenCrypt5() {
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
        String hex = CryptoUtils.toHex(CryptoUtils.hash(str));
        assertEquals(64, hex.length());
    }

    @Test
    public void toHex2() {
        String hex = CryptoUtils.toHex(CryptoUtils.aesEncrypt(str, aesKey));
        assertEquals((int) Math.ceil((double) (str.length() + 1) / 16) * 32, hex.length());
    }

}
