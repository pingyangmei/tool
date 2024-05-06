package citic.cph.tools;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessStreamCache;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @version :0.0.0.1
 * @Class : PDFUtil
 * @Description :
 * @ModificationHistory
 * @ Who           When            What
 * -----           -----           -----
 * MEI          2021/7/21       创建
 */
public class PDFUtil {

	/* 转PDF格式值 */
	private static final int wdFormatPDF = 17;
	private static final int xlFormatPDF = 0;
	private static final int ppFormatPDF = 32;
	private static final int msoTrue = -1;
	private static final int msofalse = 0;
	/* 转HTML格式值 */
	private static final int wdFormatHTML = 8;
	private static final int ppFormatHTML = 12;
	private static final int xlFormatHTML = 44;

	/* 转TXT格式值 */
	private static final int wdFormatTXT = 2;


	/**
	 * pdf合并拼接
	 *
	 * @param files      文件列表
	 * @param targetPath 合并到
	 * @return
	 * @throws IOException
	 * @Title:mulFile2One
	 * @Description: TODO
	 * @date 2019年9月22日 上午10:05:37
	 * @author yqwang
	 */
	public static File mulFile2One(List<File> files, String targetPath) {
		// pdf合并工具类
		PDFMergerUtility mergePdf = new PDFMergerUtility();
		for (File f : files) {
			if (f.exists() && f.isFile()) {
				// 循环添加要合并的pdf
				try {
					mergePdf.addSource(f);
				} catch (FileNotFoundException e) {
					LogUtil.error(e.getMessage());
				}
			}
		}
		// 设置合并生成pdf文件名称
		mergePdf.setDestinationFileName(targetPath);
		// 合并pdf
		try {
			mergePdf.mergeDocuments(null);
		} catch (IOException e) {
			LogUtil.error(e.getMessage());
		}
		return new File(targetPath);
	}

	/**
	 * 拼接2
	 *
	 * @param files
	 * @param targetPath
	 * @return
	 */
	public static String mergePdfFiles(String[] files, String targetPath) {
		Document document = null;
		try {
			document = new Document(new PdfReader(files[0]).getPageSize(1));
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(targetPath));
			document.open();
			for (String file : files) {
				PdfReader reader = new PdfReader(file);
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Objects.requireNonNull(document).close();
		}
		return targetPath;
	}


	public static void main(String[] args) {
		List<File> files = new ArrayList<>();
		files.add(new File("D:\\MEI-WORK-P14\\Desktop\\授信coe技术立项申请1.pdf"));
		files.add(new File("D:\\MEI-WORK-P14\\Desktop\\授信coe技术立项申请2.pdf"));
		File f = mulFile2One(files, "D:\\MEI-WORK-P14\\Desktop\\合并2.pdf");

//		System.out.println(f.length());

//		String inputFile = "E:\\MEI\\Desktop\\仓单模板 - 副本.docx";
//		String outputFile = "E:\\MEI\\Desktop\\转pdf.pdf";
	}
}