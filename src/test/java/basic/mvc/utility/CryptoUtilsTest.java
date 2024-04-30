package basic.mvc.utility;

import org.junit.Assert;
import org.junit.Before;
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


    @Before
    public void before() {

    }

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
    public void aesEncrypt() throws IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] res = CryptoUtils.aesEncrypt(str, aesKey);
        assertEquals(48, res.length);
        byte[] cipher = CryptoUtils.aesEncrypt(UUID.randomUUID().toString(), aesKey);
        assertNotEquals(new String(res, StandardCharsets.UTF_8), new String(cipher, StandardCharsets.UTF_8));
    }

    @Test
    public void aesDecrypt() throws IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] cipher = CryptoUtils.aesEncrypt(str, aesKey);
        String res = CryptoUtils.aesDecrypt(cipher, aesKey);
        assertEquals(str, res);
    }

}
