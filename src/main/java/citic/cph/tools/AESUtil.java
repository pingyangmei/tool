package citic.cph.tools;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * @version :0.0.0.1
 * @Class : AESUtil
 * @Description :
 * @ModificationHistory
 * @ Who           When            What
 * -----           -----           -----
 * MEI          2020/11/13       创建
 */
public class AESUtil {

	public static final String SECRET_KEY = Tool.getUUID().substring(0, 16);//目前jdk版本只支持16位秘钥（32位的需添加jar包）前后端统一的秘钥
	private static final Logger log = LoggerFactory.getLogger(AESUtil.class);
	private static String ivParameter = Objects.requireNonNull(MD5(SECRET_KEY)).substring(8, 24);//偏移量    前后端统一的偏移量// 加密

	//加密
	public static String encrypt(String content) {
		if (content == null || content.replaceAll(" ", "").equals("")) {
			return "";
		}
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
			byte[] raw = SECRET_KEY.getBytes();
			SecretKeySpec seKeySpec = new SecretKeySpec(raw, "AES");// 转换为AES专用密钥
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, seKeySpec, iv);// 初始化为加密模式的密码器
			byte[] byteResult = cipher.doFinal(content.getBytes("utf-8"));// 加密
			return Base64.getEncoder().encodeToString(byteResult); //进行base64编码
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			return "";
		} catch (UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
			log.error("AES加密错误", e);
			return "";
		}
	}

	// 解密
	public static String decrypt(String content) {
		if (content == null || content.replaceAll(" ", "").equals("")) {
			return "";
		}
		try {
			byte[] byteContent = Base64.getDecoder().decode(content);//base64解码
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
			byte[] raw = SECRET_KEY.getBytes();
			SecretKeySpec seKeySpec = new SecretKeySpec(raw, "AES");// 转换为AES专用密钥
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.DECRYPT_MODE, seKeySpec, iv);// 初始化为加密模式的密码器
			byte[] byteResult = cipher.doFinal(byteContent);// 解密
			return new String(byteResult);
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			return "";
		} catch (Exception e) {
			log.error("AES解密错误", e);
			return "";
		}
	}

	public static String MD5(String s) {
		char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

		try {
			byte[] e = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(e);
			byte[] md = mdInst.digest();
			int j = md.length;
			char[] str = new char[j * 2];
			int k = 0;
			byte[] var8 = md;
			int var9 = md.length;

			for (int var10 = 0; var10 < var9; ++var10) {
				byte byte0 = var8[var10];
				str[k++] = hexDigits[byte0 >>> 4 & 15];
				str[k++] = hexDigits[byte0 & 15];
			}

			return new String(str);
		} catch (Exception var12) {
			log.error("MD5算法异常", var12);
			return null;
		}
	}

	public static void main(String[] args) {

		System.out.println("1:" + AESUtil.SECRET_KEY);
		System.out.println("2:" + AESUtil.SECRET_KEY);
		String content = "测试入参ABC";
		System.out.println("加密之前：" + content);

		// 加密
		String jiaMi = AESUtil.encrypt(content);
		System.out.println("加密后的内容：" + jiaMi);

		// 解密
		String jieMi = AESUtil.decrypt(jiaMi);
		System.out.println("解密后的内容：" + jieMi);
	}

}
