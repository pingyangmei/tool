package citic.cph.tools;

/**
 * @version :0.0.0.1
 * @Class : MD5Util
 * @Description :
 * @ModificationHistory
 * @ Who            When            What
 * -----            -----           -----
 * MEI          2022/3/1          创建
 */
public class MD5Util {
	private MD5Util() {
	}


	public static String MD5(String s) {
		return MessageDigestHelper.digest(s, MessageDigestHelper.ALGORITHM.MD5);
	}

	public static String MD5_16(String s) {
		String md5 = MessageDigestHelper.digest(s, MessageDigestHelper.ALGORITHM.MD5);
		return md5.substring(8, 24);
	}
}
