package citic.cph.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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

    private static final String DebugLogPix = " DDDDDDDDDDDDDDDDD ";
    private static final String InfoLogPix = " >>>>>>>>>>>>>>>>>> ";
    private static final String WarnLogPix = " !!!!!!!!!!!!!!!!!! ";
    private static final String ErrorLogPix = " ××××××××××××××××× ";

    public static void debug(String format, Object... arguments) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        log.debug(DebugLogPix + stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + " 行 " + format, arguments);
    }

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

    private static void info2(String format, Object... arguments) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        log.info(InfoLogPix + stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + " 行 " + format, arguments);
    }

    public static void login(String SERVICE_NAME, Object... request) {
        LogUtil.info2(SERVICE_NAME + "调用:{}, 入参:{}", Thread.currentThread().getStackTrace()[2].getMethodName(), Tool.opj2Str(request));
    }

    public static void logout(String SERVICE_NAME, Object... response) {
        LogUtil.info2(SERVICE_NAME + "调用:{}, 出参:{}", Thread.currentThread().getStackTrace()[2].getMethodName(), Tool.opj2Str(response));
    }

    @SneakyThrows
    public static <T> T logout(String SERVICE_NAME, Object response, Class<T> clazz) {
        LogUtil.info2(SERVICE_NAME + "调用:{}, 出参:{}", Thread.currentThread().getStackTrace()[2].getMethodName(), Tool.opj2Str(response));
        return Tool.objectMapper.readValue(Objects.requireNonNull(Tool.opj2Str(response)), clazz);
    }

    /**
     * 不支持参数中有日期类型的属性字段
     *
     * @param SERVICE_NAME
     * @param response
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T logoutList(String SERVICE_NAME, Object response, TypeReference<T> valueTypeRef) {
        LogUtil.info2(SERVICE_NAME + "调用:{}, 出参:{}", Thread.currentThread().getStackTrace()[2].getMethodName(), Tool.opj2Str(response));
        return Tool.objectMapper.readValue(Objects.requireNonNull(Tool.opj2Str(response)), valueTypeRef);
    }
}