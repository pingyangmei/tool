package citic.cph.tools;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @version :0.0.0.1
 * @Class : RSAUtil
 * @Description :
 * @ModificationHistory
 * @ Who            When            What
 * -----            -----           -----
 * MEI          2022/3/1          创建
 */
public class RSAUtil {

	private static final String ENCODING = "UTF-8";
	private static final String KEY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";
	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PUBLIC_KEY_VALUE = "publicEncoded";
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	private static final String PRIVATE_KEY_VALUE = "privateEncoded";
	private static final int MAX_ENCRYPT_BLOCK = 117;
	private static final int MAX_DECRYPT_BLOCK = 128;
	private static final int KEY_SIZE = 1024;

	public RSAUtil() {
	}

	public static Map<String, Object> genKeyPair() throws Exception {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(1024);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
			Map<String, Object> keyMap = new HashMap(2);
			keyMap.put("RSAPublicKey", publicKey);
			keyMap.put("RSAPrivateKey", privateKey);
			keyMap.put("publicEncoded", Base64.getEncoder().encode(publicKey.getEncoded()));
			keyMap.put("privateEncoded", Base64.getEncoder().encode(privateKey.getEncoded()));
			return keyMap;
		} catch (NoSuchAlgorithmException var5) {
			throw new Exception("生成密钥对异常", var5);
		}
	}

	public static String getPrivateKey(Map<String, Object> keyMap) {
		Key key = (Key)keyMap.get("RSAPrivateKey");

		try {
			return new String(Base64.getEncoder().encode(key.getEncoded()), "UTF-8");
		} catch (UnsupportedEncodingException var3) {
			var3.printStackTrace();
			return null;
		}
	}

	public static String getPublicKey(Map<String, Object> keyMap) {
		Key key = (Key)keyMap.get("RSAPublicKey");

		try {
			return new String(Base64.getEncoder().encode(key.getEncoded()), "UTF-8");
		} catch (UnsupportedEncodingException var3) {
			var3.printStackTrace();
			return null;
		}
	}

	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		try {
			byte[] keyBytes = Base64.getDecoder().decode(publicKey);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicK = keyFactory.generatePublic(keySpec);
			Signature signature = Signature.getInstance("SHA256WithRSA");
			signature.initVerify(publicK);
			signature.update(data);
			return signature.verify(Base64.getDecoder().decode(sign));
		} catch (SignatureException | InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException var8) {
			throw new Exception("校验数字签名", var8);
		}
	}

	public static String sign(byte[] data, String privateKey) throws Exception {
		try {
			byte[] keyBytes = Base64.getDecoder().decode(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
			Signature signature = Signature.getInstance("SHA256WithRSA");
			signature.initSign(privateK);
			signature.update(data);
			return Base64.getEncoder().encodeToString(signature.sign());
		} catch (SignatureException | InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException var7) {
			throw new Exception("校验数字签名异常", var7);
		}
	}

	public static String encryptToStringByPublicKey(String data, String publicKey) throws Exception {
		return Base64.getEncoder().encodeToString(encryptByPublicKey(data.getBytes(), publicKey));
	}

	public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
		try {
			byte[] keyBytes = Base64.getDecoder().decode(publicKey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Key publicK = keyFactory.generatePublic(x509KeySpec);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(1, publicK);
			return doFinalWithMaxBlock(data, cipher, 117);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException var7) {
			throw new Exception("加密数据异常", var7);
		}
	}

	public static String decryptToStringByPrivateKey(String data, String privateKey) throws Exception {
		return new String(decryptByPrivateKey(Base64.getDecoder().decode(data.getBytes()), privateKey));
	}

	public static byte[] decryptByPrivateKey(byte[] data, String privateKey) throws Exception {
		try {
			byte[] keyBytes = Base64.getDecoder().decode(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(2, privateK);
			return doFinalWithMaxBlock(data, cipher, 128);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException var7) {
			throw new Exception("加密数据异常", var7);
		}
	}

	public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
		try {
			byte[] keyBytes = Base64.getDecoder().decode(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(1, privateK);
			return doFinalWithMaxBlock(data, cipher, 117);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException var7) {
			throw new Exception("加密数据异常", var7);
		}
	}

	public static byte[] decryptByPublicKey(byte[] data, String publicKey) throws Exception {
		try {
			byte[] keyBytes = Base64.getDecoder().decode(publicKey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Key publicK = keyFactory.generatePublic(x509KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(2, publicK);
			return doFinalWithMaxBlock(data, cipher, 128);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException var7) {
			throw new Exception("解密数据异常", var7);
		}
	}

	private static byte[] doFinalWithMaxBlock(byte[] data, Cipher cipher, int maxBlock) throws Exception {
		try {
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;

			for(int i = 0; inputLen - offSet > 0; offSet = i * maxBlock) {
				byte[] cache;
				if (inputLen - offSet > maxBlock) {
					cache = cipher.doFinal(data, offSet, maxBlock);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}

				out.write(cache, 0, cache.length);
				++i;
			}

			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		} catch (IllegalBlockSizeException | IOException | BadPaddingException var9) {
			throw new Exception("处理加密数据异常", var9);
		}
	}

	public static void main(String[] args) throws Exception {
		String s = "123";
		String pub = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCutOLYV4qC/gnMgui57L0Up4xl87xfqODmC4Yypv2NrgYfmu6J+GhxrlB6lAggoh3oAAgBo8bztEpNLSiEnVzr68MvQSifaetjfp0vkBtFoCDbs+Sm3cNk7AljJlbRyhLN3vWLKrS2DocBjAwmyx4D/Q/ORNZc+p8mhp+bOfSFmQIDAQAB";
		String pri = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK604thXioL+CcyC6LnsvRSnjGXzvF+o4OYLhjKm/Y2uBh+a7on4aHGuUHqUCCCiHegACAGjxvO0Sk0tKISdXOvrwy9BKJ9p62N+nS+QG0WgINuz5Kbdw2TsCWMmVtHKEs3e9YsqtLYOhwGMDCbLHgP9D85E1lz6nyaGn5s59IWZAgMBAAECgYB5OzIyl8SZE3lvPeQKqHmorH9je9fatZCicZrE6VYHC5xaMCOZABRR1nkoJjLhmj2QcP0dTi3PjOsGVPDJ3XzNXrMUWbMFHLzRcdR8Grw0AKZHDwDqzWJzjTov9hx5ZpbP8XcY1HpYWm/PUmfBr+uoWkSrPc9snlLv/U5ohjQnhQJBAOY5M7KibQjTtNjEYEGs51pg/ysTEEnCdZc/nHc6hU1vwClOiaSknv6XM2cWVhXKX1tG0AuzEYAAGX7Rvg3EOEcCQQDCRG1/vtPqrTito13JSvB+BH6ajQaAkIUNqKn6nODmn2cfXm6xir2PE84zD5uapG0tz9RmqcqWkqdSBH2ogCMfAkEAgW6VZw+0Nys22EuHRkUcCI7RxnjARoeiLrdfkxR+jyuNoGt8LOk0TjPZfVJscXHGLH3iR0GUTB9CNi9bJ0gyzwJBAKeke0XQ/Hk40H9vSiDKDa0B3esrJeBOVrOJyF2fgakjB8+XIzkM/DX7JmcD4gjaOeldvvJ1NYEov0FU5MdDBIsCQAxCqH7uw20eBfu1+06OD4a9G6RpE6FbDiVpmqc/wBCb5VEcxwn/K7ypSot/A+CMOrqZGH77wYAljPTNwlmhlnQ=";
		String es = encryptToStringByPublicKey(s, pub);
		System.out.println(es);
		String ds = decryptToStringByPrivateKey(es, pri);
		System.out.println(ds);
	}
}
