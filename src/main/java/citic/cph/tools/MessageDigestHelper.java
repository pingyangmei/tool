package citic.cph.tools;

import java.security.MessageDigest;

/**
 * @version :0.0.0.1
 * @Class : MessageDigestHelper
 * @Description :
 * @ModificationHistory
 * @ Who            When            What
 * -----            -----           -----
 * MEI          2022/3/1          创建
 */
public class MessageDigestHelper {
	private MessageDigestHelper() {
	}

	public static String digest(String s, ALGORITHM algorithm) {
		return digest(s.getBytes(), algorithm);
	}

	public static String digest(byte[] bytes, ALGORITHM algorithm) {
		char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			MessageDigest mdInst = MessageDigest.getInstance(algorithm.value());
			mdInst.update(bytes);
			byte[] md = mdInst.digest();
			int j = md.length;
			char[] str = new char[j * 2];
			int k = 0;

			for (byte byte0 : md) {
				str[k++] = hexDigits[byte0 >>> 4 & 15];
				str[k++] = hexDigits[byte0 & 15];
			}
			return new String(str);
		} catch (Exception e) {
			LogUtil.error(algorithm + "摘要异常", e);
			throw new BizException(algorithm.value() + "摘要算法计算异常");
		}
	}


	public static String md5(String s) {
		return digest(s, ALGORITHM.MD5);
	}

	public static String md5(byte[] bytes) {
		return digest(bytes, ALGORITHM.MD5);
	}

	public static String sha1(String s) {
		return digest(s, ALGORITHM.SHA1);
	}

	public static String sha1(byte[] bytes) {
		return digest(bytes, ALGORITHM.SHA1);
	}

	public static String sha256(String s) {
		return digest(s, ALGORITHM.SHA256);
	}

	public static String sha256(byte[] bytes) {
		return digest(bytes, ALGORITHM.SHA256);
	}

	public static String sha384(String s) {
		return digest(s, ALGORITHM.SHA384);
	}

	public static String sha384(byte[] bytes) {
		return digest(bytes, ALGORITHM.SHA384);
	}

	public static String sha512(String s) {
		return digest(s, ALGORITHM.SHA512);
	}

	public static String sha512(byte[] bytes) {
		return digest(bytes, ALGORITHM.SHA512);
	}

	enum ALGORITHM {

		/**
		 * MD
		 */
		MD5("MD5"),
		SHA1("SHA-1"),
		SHA256("SHA-256"),
		SHA384("SHA-384"),
		SHA512("SHA-512");

		String s;

		ALGORITHM(String s) {
			this.s = s;
		}

		String value() {
			return s;
		}
	}

	public static void main(String[] args) {
		System.out.println(md5("123"));
		System.out.println(sha1("123"));
		System.out.println(sha256("123"));
		System.out.println(sha384("123"));
		System.out.println(sha512("123"));
	}
}
