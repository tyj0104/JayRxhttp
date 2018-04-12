package library.learn.com.basetools.http;

import com.google.common.collect.Maps;


import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * OkHttp3工具类
 * Created by maoyudong on 2016/12/9.
 */

public class OkHttp3Utils {
    public static final MediaType application_octet_stream = MediaType.parse("application/octet-stream");
    public static final MediaType text_plain = MediaType.parse("text/plain");
    public static final MediaType application_x_www_form_urlencoded = MediaType.parse("application/x-www-form-urlencoded");
    public static final MediaType application_json = MediaType.parse("application/json");

    /**
     * 文件转RequestBody
     */
    public static RequestBody toRequestBody(File file) {
        return RequestBody.create(application_octet_stream, file);
    }
    public static RequestBody toRequestBody(MockFile file) {
        return RequestBody.create(application_octet_stream, file.getData());
    }

    /**
     * String转RequestBody
     */
    public static RequestBody toRequestBody(String text) {
        return RequestBody.create(text_plain, text);
    }

    /**
     * byte[] 转 RequestBody
     */
    public static RequestBody toRequestBody(byte[] bytes) {
        return RequestBody.create(application_octet_stream, bytes);
    }

    /**
     * byte[] 转 RequestBody
     */
    public static RequestBody toRequestBody(byte[] bytes, int offset, int byteCount) {
        return RequestBody.create(application_octet_stream, bytes, offset, byteCount);
    }

    /**
     * 合并请求参数
     */
    public static Request mergeRequestParams(Map<String, String> params, Request request) {
        if ("GET".equals(request.method())) {
            //GET
            HttpUrl url = request.url();
            for (int i = 0; i < url.querySize(); i++) {
                params.put(url.queryParameterName(i), url.queryParameterValue(i));
            }
            HttpUrl.Builder builder = url.newBuilder().query(null);
            for (Map.Entry<String, String> e : params.entrySet()) {
                builder.addQueryParameter(e.getKey(), e.getValue());
            }
            url = builder.build();
            return request.newBuilder().url(url).build();
        } else if ("POST".equals(request.method())) {
            //POST
            RequestBody body = request.body();
            HttpUrl url = request.url();
            if (url.querySize() > 0) {
                for (int i = 0; i < url.querySize(); i++) {
                    params.put(url.queryParameterName(i), url.queryParameterValue(i));
                }
                url = url.newBuilder().query(null).build();
                request = request.newBuilder().url(url).build();
            }
            if (body instanceof FormBody) {
                FormBody formBody = FormBody.class.cast(body);
                for (int i = 0; i < formBody.size(); i++) {
                    params.put(formBody.name(i), formBody.value(i));
                }
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, String> e : params.entrySet()) {
                    builder.add(e.getKey(), e.getValue());
                }
                body = builder.build();
            } else if (body instanceof MultipartBody) {
                MultipartBody multipartBody = MultipartBody.class.cast(body);
                Map<String, Object> _params = Maps.newLinkedHashMap(params);
                for (MultipartBody.Part part : multipartBody.parts()) {
                    _params.put(nameOf(part), part);
                }
                MultipartBody.Builder builder = new MultipartBody.Builder();
                for (Map.Entry<String, Object> e : _params.entrySet()) {
                    if (e.getValue() instanceof String) {
                        builder.addFormDataPart(e.getKey(), String.class.cast(e.getValue()));
                    } else if (e.getValue() instanceof MultipartBody.Part) {
                        builder.addPart(MultipartBody.Part.class.cast(e.getValue()));
                    }
                }
                body = builder.build();
            } else {
                throw new RuntimeException();
            }
            return request.newBuilder().post(body).build();
        } else {
            //others: HEAD、PUT、DELETE...
            return request;
        }
    }

    /**
     * 返回MultipartBody.Part的字段名称
     */
    private static String nameOf(MultipartBody.Part part) {
        String name = part.headers().values("Content-Disposition").get(0);
        String[] array = name.split("; ");
        for (String s : array) {
            if (s.startsWith("name=\"")) {
                return s.substring(6, s.length() - 1);
            }
        }
        throw new RuntimeException();
    }

    /**
     * 根据Level返回一个LoggerInterceptor
     */
    public static Interceptor newLogger(HttpLoggingInterceptor.Level level) {
        return new HttpLoggingInterceptor().setLevel(level);
    }

    /**
     * 返回一个自定义 User-Agent 的拦截器
     */
    public static Interceptor userAgentInterceptor(String userAgent) {
        return chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", userAgent)
                    .build();
            return chain.proceed(request);
        };
    }

    /**
     * GET请求转POST请求的拦截器
     */
    public static final Interceptor get2postInterceptor = chain -> {
        Request request = chain.request();
        if ("GET".equals(request.method())) {
            HttpUrl url = request.url();
            Map<String, String> params = Maps.newLinkedHashMap();
            for (int i = 0; i < url.querySize(); i++) {
                params.put(url.queryParameterName(i), url.queryParameterValue(i));
            }
            url = url.newBuilder().query(null).build();
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> e : params.entrySet()) {
                builder.add(e.getKey(), e.getValue());
            }
            request = request.newBuilder().url(url).post(builder.build()).build();
        }
        return chain.proceed(request);
    };
}
