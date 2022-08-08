package citic.cph.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

	public static final DateTimeFormatter DFY_M_D = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter DFY_M_D_H_M_S = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		// 转换为格式化的json
//		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		// 如果json中有新增的字段并且是实体类类中不存在的，不报错
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 下面配置解决LocalDateTime序列化的问题
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		JavaTimeModule javaTimeModule = new JavaTimeModule();

		//日期序列化
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DFY_M_D_H_M_S));
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DFY_M_D));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

		//日期反序列化
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DFY_M_D_H_M_S));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DFY_M_D));
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

		objectMapper.registerModule(javaTimeModule);
	}

	static void debug(String format, Object... arguments) {
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
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		LogUtil.info2(SERVICE_NAME + "调用:{}, 出参:{}", Thread.currentThread().getStackTrace()[2].getMethodName(), Tool.opj2Str(response));
		return objectMapper.readValue(Objects.requireNonNull(Tool.opj2Str(response)), clazz);
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
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		LogUtil.info2(SERVICE_NAME + "调用:{}, 出参:{}", Thread.currentThread().getStackTrace()[2].getMethodName(), Tool.opj2Str(response));
		return objectMapper.readValue(Objects.requireNonNull(Tool.opj2Str(response)), valueTypeRef);
	}
}
