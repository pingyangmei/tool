package citic.cph.tools;

import lombok.SneakyThrows;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static OkHttpClient client;
    private final static int READ_TIMEOUT = 120;
    private final static int CONNECT_TIMEOUT = 120;
    private final static int WRITE_TIMEOUT = 120;

    private final static Long LOG_LIMIT = 5000L;
    private static final List<String> noLogMethod = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public HttpUtil() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //读取超时
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        //连接超时
        clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        //写入超时
        clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        //自定义连接池最大空闲连接数和等待时间大小，否则默认最大5个空闲连接
        clientBuilder.connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES));

        //SSL设置
        clientBuilder.sslSocketFactory(createSSLSocketFactory(), new TrustAllManager());
        clientBuilder.hostnameVerifier(new TrustAllHostnameVerifier());

        client = clientBuilder.build();
    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }

        return ssfFactory;
    }

    /**
     * SSL信任所有
     */
    private class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * SSL信任所有证书
     */
    private class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            //设置为true
            return true;
        }
    }

    /**
     * (适用于请求url = baseURL+method 的情况)
     *
     * @param url          服务地址
     * @param SERVICE_NAME 服务名称
     * @param method       方法
     * @param req          请求参数
     * @return
     */
    public /*static*/ String getHttp(String url, String SERVICE_NAME, String method, String req) {
        Request request = new Request.Builder()
                .url(url + method + req)
                .get()
                .build();
        return dealRequest(request, SERVICE_NAME, method, req);
    }

    /**
     * (适用于请求url = baseURL+method 的情况)
     *
     * @param url          服务地址
     * @param SERVICE_NAME 服务名称
     * @param method       方法
     * @param req          请求参数
     * @return
     */
    public /*static*/ String getHttpWithHeaders(String url, String SERVICE_NAME, String method, String req, List<Tuple2<String, String>> headersList) {
        Headers.Builder builder = new Headers.Builder();
        headersList.forEach(i -> builder.add(i.a, i.b));
        Headers headers = builder.build();
        Request request = new Request.Builder()
                .url(url + method + req)
                .get()
                .headers(headers)
                .build();
        return dealRequest(request, SERVICE_NAME, method, req);
    }


    /**
     * (适用于请求url = baseURL+method 的情况)
     *
     * @param url          服务地址
     * @param SERVICE_NAME 服务名称
     * @param method       方法
     * @param req          请求参数
     * @return
     */
    public /*static*/ String postJson(String url, String SERVICE_NAME, String method, Object req, List<Tuple2<String, String>> headersList) {
        String reqStr = Tool.opj2Str(req);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url + method);
        /*请求头处理*/
        if (!Tool.isBlankOrNull(headersList)) {
            Headers.Builder headBuilder = new Headers.Builder();
            headersList.forEach(i -> headBuilder.add(i.a, i.b));
            Headers headers = headBuilder.build();
            LogUtil.info("postForm请求头:{}", headers.toString());
            requestBuilder.headers(headers);
        }
        if (!Tool.isBlankOrNull(reqStr)) {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.Companion.create(reqStr, mediaType);
            LogUtil.info("postForm请求参数:{}", reqStr);
            requestBuilder.post(requestBody);
        }
        Request request = requestBuilder.build();
        return dealRequest(request, SERVICE_NAME, method, reqStr);
    }


    /**
     * (适用于请求url = baseURL+method 的情况)
     *
     * @param url          服务地址
     * @param SERVICE_NAME 服务名称
     * @param method       方法
     * @param req          请求参数
     * @return
     */
    @SneakyThrows
    public /*static*/ String postForm(String url, String SERVICE_NAME, String method, Object req, List<Tuple2<String, String>> headersList) {

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url + method);
        /*请求头处理*/
        if (!Tool.isBlankOrNull(headersList)) {
            Headers.Builder headBuilder = new Headers.Builder();
            headersList.forEach(i -> headBuilder.add(i.a, i.b));
            Headers headers = headBuilder.build();
            LogUtil.info("postForm请求头:{}", headers.toString());
            requestBuilder.headers(headers);
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Field field : req.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String key = field.getName();
            String value = Tool.opj2Str(field.get(req));
            if (field.getType().equals(File.class)) {
                File file = (File) field.get(req);
                if (file != null) {
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }
            } else {
                if (Tool.isBlankOrNull(value)) {
                    continue;
                }
                builder.addFormDataPart(key, value);
            }
            LogUtil.info("postForm请求参数:{}：{}", key, value);
        }
        RequestBody requestBody = builder.build();
        requestBuilder.post(requestBody);
        Request request = requestBuilder.build();
        return dealRequest(request, SERVICE_NAME, method, requestBody.toString());
    }


    private static String dealRequest(Request request, String serviceName, String method, String reqStr) {
        if (!noLogMethod.contains(method)) {
            log.info("调用: {}, 接口方法: {}, 地址: {}, 参数: {}", serviceName, method, request.url(), reqStr);
        }
        try (Response response = client.newCall(request).execute()) {
            boolean retBoolean = response.isSuccessful();
            int code = response.code();
            String message = response.message();
            if (retBoolean) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String resStr = responseBody.string();
                    String logResStr = resStr;
                    if (logResStr.length() > LOG_LIMIT) {
                        logResStr = resStr;
                    }
                    if (!noLogMethod.contains(method)) {
                        log.info("调用: {}, 接口方法: {}, 返回内容: {}", serviceName, method, logResStr);
                    }
                    return resStr;
                }
                log.error("调用: {}, 成功, 但返回为空! 方法: {}", serviceName, method);
                throw new RuntimeException("调用" + serviceName + ", 接口方法: " + method + "方法成功!但返回为空");
            } else {
                log.error("调用: {}, 失败! 方法: {}, 返回值: {}, 返回信息: {}", serviceName, method, code, message);
                throw new RuntimeException("调用" + serviceName + ", 接口方法: " + method + "方法失败! 返回值" + code);
            }
        } catch (IOException e) {
            log.error("调用: {}, 异常! 方法: {}", serviceName, method, e);
            throw new RuntimeException("调用" + serviceName + ", 接口方法: " + method + "方法异常!");
        }
    }

}
