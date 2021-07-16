package citic.cph.tools;

import lombok.extern.slf4j.Slf4j;

/**
 * @version :0.0.0.1
 * @Class : LogUtil
 * @Description :
 * @ModificationHistory
 * @ Who           When            What
 * -----           -----           -----
 * MEI          2021/7/16       创建
 */
@Slf4j
public class LogUtil {

	private static final String InfoLogPix = " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ";
	private static final String WarnLogPix = " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ";
	private static final String ErrorLogPix = " XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX ";

	public static void info(String format, Object... arguments) {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
		log.info(InfoLogPix + stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + " 行 " + format, arguments);
	}

	public static void warn(String format, Object... arguments) {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
		log.warn(WarnLogPix + stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + " 行 " + format, arguments);
	}

	public static void error(String format, Object... arguments) {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
		log.error(ErrorLogPix + stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + " 行 " + format, arguments);
	}
}
