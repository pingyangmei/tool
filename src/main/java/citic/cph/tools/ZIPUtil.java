package citic.cph.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @version :0.0.0.1
 * @Class : ZIPUtil
 * @Description :
 * @ModificationHistory
 * @ Who           When            What
 * -----           -----           -----
 * MEI          2021/3/9       创建
 */
public class ZIPUtil {

	public static void zipFiles(List<File> srcFiles, File zipFile) {
		// 判断压缩后的文件存在不，不存在则创建
		if (!zipFile.exists()) {
			try {
				zipFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 创建 FileOutputStream 对象
		FileOutputStream fileOutputStream;
		// 创建 ZipOutputStream
		ZipOutputStream zipOutputStream;
		// 创建 FileInputStream 对象
		FileInputStream fileInputStream = null;

		try {
			// 实例化 FileOutputStream 对象
			fileOutputStream = new FileOutputStream(zipFile);
			// 实例化 ZipOutputStream 对象
			zipOutputStream = new ZipOutputStream(fileOutputStream);
			// 创建 ZipEntry 对象
			ZipEntry zipEntry;
			// 遍历源文件数组
			for (File srcFile : srcFiles) {
				// 将源文件数组中的当前文件读入 FileInputStream 流中
				fileInputStream = new FileInputStream(srcFile);
				// 实例化 ZipEntry 对象，源文件数组中的当前文件
				zipEntry = new ZipEntry(srcFile.getName());
				zipOutputStream.putNextEntry(zipEntry);
				// 该变量记录每次真正读的字节个数
				int len;
				// 定义每次读取的字节数组
				byte[] buffer = new byte[1024];
				while ((len = fileInputStream.read(buffer)) > 0) {
					zipOutputStream.write(buffer, 0, len);
				}
			}
			zipOutputStream.closeEntry();
			zipOutputStream.close();
			assert fileInputStream != null;
			fileInputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		List<File> srcFiles = new ArrayList<>();
		srcFiles.add(new File("E:\\质押合同.pdf"));
		srcFiles.add(new File("E:\\流贷协议.pdf"));
		File zipFile = new File("E:\\ZipFile222.zip");
		// 调用压缩方法
		zipFiles(srcFiles, zipFile);
	}
}
