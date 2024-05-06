package citic.cph.tools.excel.util.poi;

import citic.cph.tools.BizException;
import citic.cph.tools.LogUtil;
import citic.cph.tools.Tool;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @version :0.0.0.1
 * @Class : ExcelUtil
 * @Description :
 * @ModificationHistory
 * @ Who           When            What
 * -----           -----           -----
 * MEI          2021/7/16       创建
 */
public class ExcelUtil {


	private static String getStringValue(Cell cell) {
		String s = null;
		if (cell.getCellType() == CellType.NUMERIC) {
			long longVal = Math.round(cell.getNumericCellValue());
			double doubleVal = cell.getNumericCellValue();
			if (Double.parseDouble(longVal + ".0") == doubleVal) {
				cell.setCellType(CellType.STRING);
			}
			s = cell.getStringCellValue();
		}
		if (CellType.NUMERIC == cell.getCellType()) {
			s = String.valueOf(cell.getNumericCellValue());
		} else if (CellType.STRING == cell.getCellType()) {
			s = cell.getStringCellValue();
		}
		if ("".equals(s) || s == null) {
			return null;
		}
		return s;
	}

	/* 复制后个性化使用 */
	public Object importExcel1(MultipartFile file) throws Exception {
		/* 对象 */
		List<Object> t = new ArrayList<>();

		Workbook workbook = ExcelPOIUtils.getWorkBook(file);
		/* 获取sheet */
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			throw new BizException("导入模板无内容!");
		}
		/* getPhysicalNumberOfRows 获取有记录的行数 */
		LogUtil.info("共有多少行:{}", sheet.getPhysicalNumberOfRows());
		for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
			/* 初始化每行一个对象 */
			Object h = new Object();
			LogUtil.info("第{}行", j);
			Row row = sheet.getRow(j);
			if (null == row) {
				break;
			}
			/* 序号没有终止读取*/
			Cell cell = row.getCell(0);
			if (cell == null || Tool.isBlankOrNull(getStringValue(cell))) {
				break;
			}
			/* getLastCellNum 获取最后一列 */
			LogUtil.info("共有多少列:{}", row.getLastCellNum());
			for (int k = 1; k < row.getLastCellNum(); k++) {
				LogUtil.info("第{}列数据:{}", k, row.getCell(k));
				// TODO: 2021/7/16  在这里解析每一行的每一列,处理
			}
			t.add(h);
		}
		LogUtil.info("导入结果:{}", Tool.opj2Str(t));
		return t;
	}
}
