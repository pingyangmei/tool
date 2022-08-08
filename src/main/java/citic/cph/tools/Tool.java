package citic.cph.tools;

import com.alibaba.fastjson.JSONObject;
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
import org.apache.commons.beanutils.BeanUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version :0.0.0.1
 * @Class : citic.cph.tools.Tool
 * @Description : 工具类
 * @ModificationHistory 记录
 * @ Who           When            What
 * -----           -----           -----
 * MEI          2020/5/8       创建
 */
public class Tool {
    /**
     * hash类型
     */
    public static final String HASH_TYPE_MD5 = "MD5";
    public static final String HASH_TYPE_SHA1 = "SHA-1";
    public static final String HASH_TYPE_SHA256 = "SHA-256";
    public static final String HASH_TYPE_SHA384 = "SHA-384";
    public static final String HASH_TYPE_SHA512 = "SHA-512";
    /**
     * 时间类
     */
    public static final DateTimeFormatter DFYMD = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter DFYMDHMS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter DFYMDHMSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static final DateTimeFormatter DFY_M_D = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DFY_M_D_H_M_S = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private static final SimpleDateFormat SFYMD = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat SFYMDHMS = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat SFYMDHMSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static final SimpleDateFormat SFHMS = new SimpleDateFormat("HHmmss");

    private static final SimpleDateFormat SFY_M_D = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SFY_M_D_H_M_S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SFH_M_S = new SimpleDateFormat("HH:mm:ss");
    /* 月份 */
    private final static String[] MONTH_STR = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    public static String FILE_NAME_MID_STR = "*_*";
    private static String localIp;
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

    public static String getMonthStr(int monthValue) {
        return MONTH_STR[monthValue - 1];
    }

    public static String getContent(Object... strs) {
        if (strs == null) {
            return "NA";
        }
        StringBuilder sb = new StringBuilder();
        for (Object str : strs) {
            sb.append(str);
            sb.append("|");
        }
        return sb.toString();
    }

    /**
     * 取调用RMI的客户端的主机IP
     *
     * @return 客户端主机ip
     */
    public static String getRMIClientIP() throws ServerNotActiveException {
        return RemoteServer.getClientHost();
    }

    /**
     * 取本机Ip
     *
     * @return 本地ip
     */
    public static String getLocalIp() {
        try {
            if (localIp == null) {
                localIp = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (UnknownHostException e) {
            localIp = "127.0.0.1";
            LogUtil.error("取本机IP失败", e);
        }
        return localIp;
    }

    /**
     * YMD转localdate
     *
     * @param dateStr 年月日
     * @return LocalDate
     */
    public static LocalDate strToLocalDateByYMD(String dateStr) {
        if (dateStr != null) {
            try {
                return LocalDate.parse(dateStr, DFYMD);
            } catch (DateTimeParseException e) {
                LogUtil.error("参数格式错误!" + dateStr, e);
                return LocalDate.now();
            }
        }
        return LocalDate.of(0, 1, 1);
    }

    /**
     * Y_M_D转localdate
     *
     * @param dateStr 年-月-日
     * @return LocalDate
     */
    public static LocalDate strToLocalDateByY_M_D(String dateStr) {
        if (dateStr != null) {
            try {
                return LocalDate.parse(dateStr, DFY_M_D);
            } catch (DateTimeParseException e) {
                LogUtil.error("参数格式错误!" + dateStr, e);
                return LocalDate.now();
            }
        }
        return LocalDate.of(0, 1, 1);
    }

    /********************************************************************日期时间相关*************************************************************************/

    /**
     * YMDHMS转localdateTime
     *
     * @param dateStr 年月日时分秒
     * @return LocalDateTime
     */
    public static LocalDateTime strToLocalDateByYMDHMS(String dateStr) {
        if (dateStr != null) {
            try {
                return LocalDateTime.parse(dateStr, DFYMDHMS);
            } catch (DateTimeParseException e) {
                LogUtil.error("参数格式错误!" + dateStr, e);
            }
        }
        return LocalDateTime.of(0, 1, 1, 0, 0);
    }

    /**
     * Y_M_D_H_M_S转localdateTime
     *
     * @param dateStr 年-月-日 时:分:秒
     * @return LocalDateTime
     */
    public static LocalDateTime strToLocalDateByY_M_D_H_M_S(String dateStr) {
        if (dateStr != null) {
            try {
                return LocalDateTime.parse(dateStr, DFY_M_D_H_M_S);
            } catch (DateTimeParseException e) {
                LogUtil.error("参数格式错误!" + dateStr, e);
            }
        }
        return LocalDateTime.of(0, 1, 1, 0, 0);
    }

    /**
     * date2LocalDateTime
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * date2LocalDate
     *
     * @param date date
     * @return LocalDate
     */
    public static LocalDate date2LocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 获取当月最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取当年某月最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfMonth(Integer monthValue) {
        LocalDate firstDayOfThisMonth = LocalDate.of(LocalDate.now().getYear(), monthValue, 1);
        return firstDayOfThisMonth.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取某年某月最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfMonth(Integer yearValue, Integer monthValue) {
        LocalDate firstDayOfThisMonthOfThisYear = LocalDate.of(yearValue, monthValue, 1);
        return firstDayOfThisMonthOfThisYear.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取当前对应季度最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfQuarter() {
        LocalDate now = LocalDate.now();
        return getLastDateOfQuarter(now.getYear(), now.getMonthValue());
    }

    /**
     * 获取当年某月对应季度最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfQuarter(Integer monthValue) {
        LocalDate now = LocalDate.now();
        return getLastDateOfQuarter(now.getYear(), monthValue);
    }

    /**
     * 获取某年某月对应季度最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfQuarter(Integer yearValue, Integer monthValue) {
        if (monthValue == 1
                || monthValue == 2
                || monthValue == 3
        ) {
            return LocalDate.of(yearValue, Month.MARCH, 31);
        } else if (monthValue == 4
                || monthValue == 5
                || monthValue == 6) {
            return LocalDate.of(yearValue, Month.JUNE, 30);
        } else if (monthValue == 7
                || monthValue == 8
                || monthValue == 9) {
            return LocalDate.of(yearValue, Month.SEPTEMBER, 30);
        } else {
            return LocalDate.of(yearValue, Month.NOVEMBER, 31);
        }
    }


    /**
     * 获取当年最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfYear() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 获取某年最后一天
     *
     * @return
     */
    public static LocalDate getLastDateOfYear(Integer yearValue) {
        LocalDate firstDayOfThisYear = LocalDate.of(yearValue, 1, 1);
        return firstDayOfThisYear.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 格式化输出系统日期
     *
     * @param date 系统日期,格式如：2002-12-16
     * @return 格式化的系统日期, 输出格式如：20021216[yyyyMMdd]
     */
    public static String dateToStrPattern(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化输出日期
     *
     * @return 输出格式如：yyyyMMdd
     */
    public static String dateToStrYMD(Date date) {
        if (date == null) {
            return "";
        }
        return SFYMD.format(date);
    }

    /**
     * 格式化输出日期
     *
     * @return 输出格式如：yyyy-MM-dd
     */
    public static String dateToStr_YMD(Date date) {
        if (date == null) {
            return "";
        }
        return SFY_M_D.format(date);
    }

    /**
     * 格式化输出日期
     *
     * @return 输出格式如：yyyyMMddHHmmss
     */
    public static String dateToStrYMDHMS(Date date) {
        if (date == null) {
            return "";
        }
        return SFYMDHMS.format(date);
    }

    /**
     * 格式化输出日期
     *
     * @return 输出格式如：yyyyMMddHHmmssSSS
     */
    public static String dateToStrYMDHMSS(Date date) {
        if (date == null) {
            return "";
        }
        return SFYMDHMSS.format(date);
    }

    /**
     * 格式化输出日期
     *
     * @return 输出格式如：yyyy-MM-dd HH:mm:ss
     */
    public static String dateToStr_YMDHMS(Date date) {
        if (date == null) {
            return "";
        }
        return SFY_M_D_H_M_S.format(date);
    }

    /**
     * 格式化输出时间
     *
     * @return 输出格式如：HHmmss
     */
    public static String dateToStrHMS(Date date) {
        if (date == null) {
            return "";
        }
        return SFHMS.format(date);
    }

    /**
     * 格式化输出时间
     *
     * @return 输出格式如：HH:mm:ss
     */
    public static String dateToStr_HMS(Date date) {
        if (date == null) {
            return "";
        }
        return SFH_M_S.format(date);
    }


    /**
     * ymd转date
     *
     * @param ymd 年月日
     * @return Date
     */
    public static Date strToDateYMD(String ymd) {
        if (!isBlankOrNull(ymd)) {
            try {
                return SFYMD.parse(ymd);
            } catch (ParseException e) {
                LogUtil.error("日期格式不正确" + ymd, e);
            }
        }
        return null;
    }

    /**
     * y_m_d转date
     *
     * @param y_m_d 年-月-日
     * @return Date
     */
    public static Date strToDateY_M_D(String y_m_d) {
        if (!isBlankOrNull(y_m_d)) {
            try {
                return SFY_M_D.parse(y_m_d);
            } catch (ParseException e) {
                LogUtil.error("日期格式不正确" + y_m_d, e);
            }
        }
        return null;
    }

    /**
     * ymdHms转date
     *
     * @param ymdHms 年月日时分秒
     * @return Date
     */
    public static Date strToDateYMDHMS(String ymdHms) {
        if (!isBlankOrNull(ymdHms)) {
            try {
                return SFYMDHMS.parse(ymdHms);
            } catch (ParseException e) {
                LogUtil.error("日期格式不正确" + ymdHms, e);
            }
        }
        return null;
    }

    /**
     * ymdHmsS转date
     *
     * @param ymdHmsS 年月日时分秒毫秒
     * @return Date
     */
    public static Date strToDateYMDHMSS(String ymdHmsS) {
        if (!isBlankOrNull(ymdHmsS)) {
            try {
                return SFYMDHMSS.parse(ymdHmsS);
            } catch (ParseException e) {
                LogUtil.error("日期格式不正确" + ymdHmsS, e);
            }
        }
        return null;
    }

    /**
     * y_m_d_H_m_s转date
     *
     * @param y_m_d_H_m_s 年-月-日 时:分:秒
     * @return Date
     */
    public static Date strToDateY_M_D_H_M_S(String y_m_d_H_m_s) {
        if (!isBlankOrNull(y_m_d_H_m_s)) {
            try {
                return SFY_M_D_H_M_S.parse(y_m_d_H_m_s);
            } catch (ParseException e) {
                LogUtil.error("日期格式不正确" + y_m_d_H_m_s, e);
            }
        }
        return null;
    }

    /**
     * 取参数日期的最晚时间
     */
    public static Date get12PMTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
        return cal.getTime();
    }

    /**
     * 取参数日期的最晚时间
     */
    public static LocalDateTime getLastTime(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /**
     * 取参数日期的下午3点
     */
    public static Date get3PMTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 15, 0, 0);
        return cal.getTime();
    }

    /**
     * 取参数日期的凌晨零点
     */
    public static Date get0AMTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        return cal.getTime();
    }

    public static boolean isBetween2Times(Date currentTime, String startTime, String endTime) throws Exception {
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
        startTime = dateString + " " + startTime;
        endTime = dateString + " " + endTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return currentTime.compareTo(sdf.parse(startTime)) >= 0 && currentTime.compareTo(sdf.parse(endTime)) <= 0;
    }

    /**
     * 查询两个日期相隔多少天
     */
    public static int getDaysOfTwoDate(Date beginDate, Date endDate) {
        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();

        calst.setTime(beginDate);
        caled.setTime(endDate);

        calst.set(Calendar.HOUR_OF_DAY, 0);
        calst.set(Calendar.MINUTE, 0);
        calst.set(Calendar.SECOND, 0);
        caled.set(Calendar.HOUR_OF_DAY, 0);
        caled.set(Calendar.MINUTE, 0);
        caled.set(Calendar.SECOND, 0);

        return ((int) (caled.getTime().getTime() / 1000) - (int) (calst.getTime().getTime() / 1000)) / 3600 / 24;
    }

    /**
     * 某个日期加几天
     */
    public static Date addDays(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, amount);
        return cal.getTime();
    }

    /**
     * 某个日期加几小时
     */
    public static Date addHour(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, amount);
        return cal.getTime();
    }

    /**
     * 某个日期加几分钟
     */
    public static Date addMinute(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, amount);
        return cal.getTime();
    }

    /**
     * 对于给定的日期表达式，返回指定数目的天数以前或以后的日期。
     *
     * @param date date
     * @param day  day
     */
    public static Date GoDate(Date date, int day) {
        long time = date.getTime();
        time += (long) day * 24 * 60 * 60 * 1000;
        return new Date(time);
    }

    /**
     * 获取json字符串中指定key的对象;
     *
     * @param jsonStr jsonStr
     * @param name    name
     * @return Object
     */
    public static Object getFeidFromJson(String jsonStr, String name) {// jsonStr:要转换的json字符串,name：需要的字段名称
        JSONObject json = JSONObject.parseObject(jsonStr);
        return json.get(name);
    }

    /******************************************************************日期时间相关**************************************************************************/

    /**
     * map转对象
     *
     * @param bean bean
     * @param map  map
     */
    public static void mapToObject(final Object bean, final Map<String, ?> map) {
        try {
            BeanUtils.populate(bean, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogUtil.error("对象转换异常", e);
        }
    }

    /**
     * 将对象转为map集合
     *
     * @param obj obj
     * @return Map
     */
    public static Map<String, String> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo
                .getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0
                    || key.compareToIgnoreCase("method") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            map.put(key, String.valueOf(value));
        }
        LogUtil.debug("将对象转为map集合,map=" + map);
        return map;
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj obj
     * @return Map
     */
    public static Map<String, String> objectToMap2(Object obj) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String value = String.valueOf(field.get(obj));
            map.put(fieldName, value);
        }
        return map;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /* 处理字符串中的[\等字符串 */
    public static String dealString(String args) {
        return args.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\\\", "");
    }

    /* 处理 带率 字段 */
    public static BigDecimal dealRateNum(String args) {
        return Tool.getBigDecimal(args).divide(new BigDecimal(100), 5, RoundingMode.HALF_UP);
    }

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isPhoneLegal(String str) {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    private static boolean isChinaPhoneLegal(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    private static boolean isHKPhoneLegal(String str) {
        String regExp = "^(5|6|8|9)\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 将对象转换成BigDecimal
     *
     * @param value value
     * @return double
     */
    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = BigDecimal.ZERO;
        if (!isBlankOrNull(value) && !"null".equals(value)) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }

    /****************************************************************对象转数字*****************************************************/

    /**
     * object 转 Integer
     */
    public static Integer getInteger(Object obj) {
        return Integer.valueOf((obj == null ? "0" : obj).toString().trim());
    }

    /**
     * object 转 Long
     */
    public static Long getLong(Object obj) {
        return Long.valueOf((obj == null ? "0" : obj).toString().trim());
    }

    /**
     * object 转 Double
     */
    public static Double getDouble(Object obj) {
        return Double.valueOf((obj == null ? "0" : obj).toString().trim());
    }

    /**
     * 把双精度浮点数转换为字符串
     *
     * @param num num
     * @return String
     */
    public static String fmtDouble(double num) {
        return String.valueOf(BigDecimal.valueOf(num));
    }

    /**
     * 格式化双精度浮点数，保留小数点后两位
     *
     * @param num num
     * @return String
     */
    public static String fmtDouble2(double num) {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getNumberInstance();
        nf.applyPattern("###0.00");
        return nf.format(num);
    }

    /**
     * 保留scale位小数
     *
     * @param amount amount
     * @param scale  scale
     * @return BigDecimal
     */
    public static BigDecimal roundByScale(BigDecimal amount, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new BigDecimal(new DecimalFormat("0").format(amount));
        }
        if (Tool.isBlankOrNull(amount)) {
            amount = BigDecimal.ZERO;
        }
        String as = amount.toString();
        if (!as.contains(".")) {
            as += ".0";
        }
        int count = as.split("\\.")[1].length();
        if (count > scale) {
            return amount.setScale(scale, BigDecimal.ROUND_HALF_UP);
        } else {
            StringBuilder formatStr = new StringBuilder("0.");
            for (int i = 0; i < scale; i++) {
                formatStr.append("0");
            }
            String ret = new DecimalFormat(formatStr.toString()).format(amount);
            return new BigDecimal(ret);
        }
    }

    public static boolean isBlankOrNull(Object str) {
        return str == null || str.toString().trim().isEmpty();
    }

    public static String opj2Str(Object o) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        if (o == null) {
            return null;
        }
        try {
            return o instanceof String ? (String) o : objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            LogUtil.error("对象转换异常", e);
            return "";
        }
    }

    public static String opj2StrExcludeNull(Object o) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        if (o == null) {
            return "";
        }
        try {
            return o instanceof String ? (String) o : objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            LogUtil.error("对象转换异常", e);
            return "";
        }
    }

    /**
     * 转对象
     *
     * @param o
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T str2Opj(String o, Class<T> clazz) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        if (o == null) {
            return null;
        }
        try {
            return objectMapper.readValue(o, clazz);
        } catch (Exception e) {
            LogUtil.error("对象转换异常", e.getMessage());
            return null;
        }
    }

    /**
     * 转list
     *
     * @param o
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    public static <T> T str2Opj(String o, TypeReference<T> valueTypeRef) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        if (o == null) {
            return null;
        }
        try {
            return objectMapper.readValue(o, valueTypeRef);
        } catch (Exception e) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            LogUtil.error("对象转换异常:{}, 文件:{}, 行数:{}", e.getMessage(), stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
            LogUtil.error("文件:{}, 行数:{}, 对象转换异常:{}, ", stackTraceElement.getClassName(), stackTraceElement.getLineNumber(), e.getMessage());
            return null;
        }
    }

    /* OBJ转指定对象 */
    public static <T> T opj2T(Object o, Class<T> clazz) {
        return str2Opj(opj2Str(o), clazz);
    }

    /**
     * 排空处理
     */
    public static String excludeNull(Object obj) {
        return (obj == null ? "" : obj).toString().trim();
    }

    /**
     * 获取列表最后一个元素
     *
     * @param list
     * @param <E>
     * @return
     */
    public static <E> E getLastElement(List<E> list) {
        if ((list != null) && (!list.isEmpty())) {
            int lastIdx = list.size() - 1;
            return list.get(lastIdx);
        } else
            return null;
    }

    /**
     * 验证字符串是否为n位纯数字
     *
     * @param str str
     * @return boolean
     */
    public static boolean isNum(String str, int n) {
        if (str == null) {
            return false;
        }
        if (n > 0) {
            if (str.length() != n) {
                return false;
            }
        }
        return str.matches("^\\d{" + n + "}$");
    }

    /**
     * 验证val1是否是val2的整数倍
     *
     * @param val1 val1
     * @param val2 val2
     * @return boolean
     */
    public static boolean isIntegerMulBigDecimal(BigDecimal val1, BigDecimal val2) {
        if (val1 == null || val2 == null || val2.equals(BigDecimal.ZERO)) {
            return false;
        }
        String modVal = val1.divideAndRemainder(val2)[1].toString();
        return modVal.equals("0");
    }

    /**
     * 验证val1是否是val2的整数倍
     *
     * @param val1 val1
     * @param val2 val2
     * @return boolean
     */
    public static boolean isIntegerMul(Long val1, Long val2) {
        if (val1 == null || val2 == null || String.valueOf(val2).equals("0")) {
            return false;
        }
        BigDecimal bigDecilVal1 = BigDecimal.valueOf(val1);
        BigDecimal bigDecilVal2 = BigDecimal.valueOf(val2);
        String modVal = bigDecilVal1.divideAndRemainder(bigDecilVal2)[1].toString();
        return modVal.equals("0");
    }

    /**
     * 把数字转换成资金格式
     */
    public static String fmtMoney(double money) {
        String result = formatToMoney(money);
        result = result.replaceAll("￥|$", "");
        return result;
    }

    /**
     * 把数字转换成资金格式
     */
    private static String formatToMoney(double money) {
        Locale.setDefault(Locale.CHINA);
        NumberFormat numberFormate;
        numberFormate = NumberFormat.getCurrencyInstance();
        return numberFormate.format(money);
    }

    /**
     * 将异常信息导入字符串
     *
     * @param e 异常信息
     * @return String
     */
    public static String getExceptionTrace(Throwable e) {
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
        return "No Exception";
    }

    /**
     * 补全url最后的/
     * 有则不处理
     *
     * @param url url
     * @return String
     */
    public static String completionUrlWithSprit(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }

    /**
     * 首先进行入参检查防止出现空指针异常
     * 如果两个参数都为空，则返回true
     * 如果有一项为空，则返回false
     * 接着对第一个list进行遍历，如果某一项第二个list里面没有，则返回false
     * 还要再将两个list反过来比较，因为可能一个list是两一个list的子集
     * 如果成功遍历结束，返回true
     *
     * @param l0 L1
     * @param l1 L2
     * @return boolean
     */
    public static boolean isSListEqual(List<String> l0, List<String> l1) {
        if (l0 == l1) {
            return true;
        }
        if (l0 == null || l1 == null) {
            return false;
        }
        if (l0.size() != l1.size()) {
            return false;
        }

        Collections.sort(l1);
        Collections.sort(l0);
        for (int i = 0; i < l0.size(); i++) {
            if (!l0.get(i).equals(l1.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 首先进行入参检查防止出现空指针异常
     * 如果两个参数都为空，则返回true
     * 如果有一项为空，则返回false
     * 接着对第一个list进行遍历，如果某一项第二个list里面没有，则返回false
     * 还要再将两个list反过来比较，因为可能一个list是两一个list的子集
     * 如果成功遍历结束，返回true
     *
     * @param l0 L1
     * @param l1 L2
     * @return boolean
     */
    public static boolean isListEqual(List l0, List l1) {
        if (l0 == l1) {
            return true;
        }
        if (l0 == null || l1 == null) {
            return false;
        }
        if (l0.size() != l1.size()) {
            return false;
        }
        for (Object o : l0) {
            if (!l1.contains(o)) {
                return false;
            }
        }
        for (Object o : l1) {
            if (!l0.contains(o)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成临时文件名
     *
     * @param orgPath 原始文件名
     * @return 新临时文件名
     */
    public static String getTemFileName(String orgPath) {
        String subFix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("Hms"));
        String sub = FILE_NAME_MID_STR + subFix;
        String prefix = orgPath.split("\\.")[0];
        String suffix = "." + orgPath.split("\\.")[1];
        if (prefix.contains(FILE_NAME_MID_STR)) {
            prefix = prefix.substring(0, prefix.indexOf(FILE_NAME_MID_STR));
        }
        return prefix + sub + suffix;
    }

    public static byte[] downloadFile(String url, String filePath, String fileName) {
        saveUrlAs(url, filePath, fileName);
        filePath = completionUrlWithSprit(filePath);
        return Tool.file2byte(filePath + fileName);
    }

    /**
     * @param url      下载地址
     * @param filePath 保存目录
     * @param fileName 文件名
     */
    public static void saveUrlAs(String url, String filePath, String fileName) {
        //创建不同的文件夹目录
        File file = new File(filePath);
        //判断文件夹是否存在
        if (!file.exists()) {
            //如果文件夹不存在，则创建新的的文件夹
            boolean ifMkSuccess = file.mkdirs();
            if (!ifMkSuccess) {
                LogUtil.info("文件夹" + filePath + "创建失败!");
            }
        }
        FileOutputStream fileOut;
        HttpURLConnection conn;
        InputStream inputStream;
        // 建立链接
        try {
            URL httpUrl = new URL(url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            //以Post方式提交表单，默认get方式
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            //连接指定的资源
            conn.connect();
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new Exception("文件读取失败");
            }
            //获取网络输入流
            inputStream = conn.getInputStream();
            BufferedInputStream in = new BufferedInputStream(inputStream);
            //判断文件的保存路径后面是否以/结尾
            filePath = completionUrlWithSprit(filePath);
            //写入到文件（注意文件保存路径的后面一定要加上文件的名称）
            fileOut = new FileOutputStream(filePath + fileName);
            BufferedOutputStream out = new BufferedOutputStream(fileOut);

            byte[] buf = new byte[4096];
            int length = in.read(buf);
            //保存文件
            while (length != -1) {
                out.write(buf, 0, length);
                length = in.read(buf);
            }
            out.close();
            in.close();
            conn.disconnect();
        } catch (Exception e) {
            LogUtil.error("下载失败", e);
            throw new BizException("下载失败", e);
        }
    }

    /**
     * 文件转byte[]
     *
     * @return
     */
    public static byte[] file2byte(String filePath) {
        InputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            LogUtil.error("FileNotFoundException", e);
            throw new RuntimeException(e.getMessage());
        }
        try {
            return input2byte(in);
        } catch (IOException e) {
            LogUtil.error("IOException", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 输入流转byte[]
     *
     * @return
     */
    public static byte[] input2byte(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 4];
        int rc;
        while ((rc = in.read(buff)) != -1) {
            out.write(buff, 0, rc);
        }
        byte[] in2b = out.toByteArray();
        in.close();
        out.close();
        return in2b;
    }

    /**
     * 将byte[]转为文件
     */
    public static File byte2File(byte[] buf, String filePath, String fileName) {
        File dir = new File(filePath);
        if (!dir.exists() && dir.isDirectory()) {
            LogUtil.info("文件夹{}创建是否成功:{}", filePath, dir.mkdir());
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = new File(filePath + File.separator + fileName);
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
            return file;
        } catch (Exception e) {
            LogUtil.error("异常!", e);
            throw new RuntimeException("文件转换异常!");
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    LogUtil.error("异常!", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LogUtil.error("异常!", e);
                }
            }
        }
    }

    /**
     * 文件转化成base64字符串
     * 将文件转化为字节数组字符串，并对其进行Base64编码处理
     */
    public static String getFileStr(String filePath) {
        InputStream in = null;
        byte[] data = null;
        // 读取文件字节数组
        try {
            in = new FileInputStream(filePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回 Base64 编码过的字节数组字符串
        assert data != null;
        return encoder.encode(data);
    }

    /**
     * base64字符串转化成文件，可以是JPEG、PNG、TXT和AVI等等
     *
     * @param base64FileStr
     * @param filePath
     * @return
     * @throws Exception
     */
    public static File generateFile(String base64FileStr, String filePath) {
        // 数据为空
        if (base64FileStr == null) {
            LogUtil.error("文件流为空!");
            return null;
        }
        File file = new File(filePath);
        BASE64Decoder decoder = new BASE64Decoder();

        // Base64解码,对字节数组字符串进行Base64解码并生成文件
        byte[] byt;
        try {
            byt = decoder.decodeBuffer(base64FileStr);
        } catch (IOException e) {
            LogUtil.error("文件转流失败", e);
            return null;
        }
        for (int i = 0, len = byt.length; i < len; ++i) {
            // 调整异常数据
            if (byt[i] < 0) {
                byt[i] += 256;
            }
        }
        OutputStream out = null;
        InputStream input = new ByteArrayInputStream(byt);
        try {
            // 生成指定格式的文件
            out = new FileOutputStream(filePath);
            byte[] buff = new byte[1024];
            int len;
            while ((len = input.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
        } catch (IOException e) {
            LogUtil.error("流转文件失败", e);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                LogUtil.error("流转关闭失败", e);
            }
        }
        return file;
    }

    /**
     * 获取文件名称
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    public static String getEvnValue(String key) {
        return System.getenv(key) == null ? "" : System.getenv(key);
    }

    /**
     * 获取新单号
     *
     * @param oldNo    原单号
     * @param preCount 前缀长度(保持不变的部分)
     * @param suffix   后缀长度(顺序增加的部分)
     *                 eg: old:IBN20191001000009
     *                 new:IBN20191001000010
     */
    public static String getNextSeq(String oldNo, int preCount, int suffix) {
        StringBuilder newNo = new StringBuilder(oldNo.substring(0, preCount));
        int seq = Integer.valueOf(oldNo.substring(preCount, oldNo.length()));
        int newSeq = seq + 1;
        int count0 = suffix - (String.valueOf(newSeq).length());
        for (int i = 0; i < count0; i++) {
            newNo.append("0");
        }
        newNo.append(String.valueOf(newSeq));
        return newNo.toString();
    }

    public static String getRootPath() {
        String path = Tool.class.getResource("/").toString();
        String[] patharray = path.split("/");
        return patharray[0] + "///" + patharray[1] + "/";
    }

    public static Boolean isEqual(List<Map<String, String>> list1, List<Map<String, String>> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (Map<String, String> aList1 : list1) {
            if (!list2.contains(aList1)) {
                LogUtil.info("list2中没有{}", aList1);
                return false;
            }
        }
        return true;
    }

    public static String createOpenId() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String s = MD5Util.MD5_16("ccbft" + "Baeav508hz&IvYSD" + now);
        System.out.println("OpenId: " + s);
        return s;
    }
    @SneakyThrows
    public static void createKey() {
        Map<String, Object> map = RSAUtil.genKeyPair();
        RSAUtil.getPublicKey(map);
        RSAUtil.getPrivateKey(map);
        System.out.println("publicKey: " + RSAUtil.getPublicKey(map));
        System.out.println("privateKey: " + RSAUtil.getPrivateKey(map));
    }

    @SneakyThrows
    public static Map<String, String> getTemplateArgs(Object object) {
        Map<String, String> taMap = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String key = field.getName();
            String value = Tool.opj2Str(field.get(object));
            if (Tool.isBlankOrNull(value)) {
                continue;
            }
            taMap.put(key, value);
        }
        LogUtil.info("消息参数:{}", Tool.opj2Str(taMap));
        return taMap;
    }
    public static void main(String[] args) {

		for (int i = 0; i < 10; i++) {
			System.out.println(getUUID());
		}
//        System.out.println(getMonthStr(LocalDate.now().getMonthValue()));
    }

    /* 特殊split分割字符*/
    public static final class SplitChar {
        public static final String 点 = "\\.";
        public static final String 竖 = "\\|";
        public static final String 星 = "\\*";
    }

}
