package library.learn.com.basetools.http;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import org.apache.commons.codec.binary.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Retrofit;

import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * import
 * Http异步请求工具类
 * Created by jay on 2017/6/8.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RxHttp {
    private static final Map<String, String> EMPTY_HEADERS = Collections.emptyMap();
    @Setter
    private static boolean debug = true;
    @Setter
    private static SSLConfig defaultSSLConfig = SSLConfig.newBuilder().trustAll(true).create();
    private static String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";
    private static int defaultRetryTimes = 2;
    @Setter
    private static String defaultUserAgent = okhttp3.internal.Version.userAgent();
    @Getter
    private static final RxHttp defaultInstance = newBuilder().create();

    /**
     * RxHttp构造辅助类
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private RxHttp rxHttp = new RxHttp();

        /**
         * 时间格式
         */
        public Builder setDateFormat(String dateFormat) {
            rxHttp.dateFormat = dateFormat;
            return this;
        }

        /**
         * 出错重试次数
         */
        public Builder setRetryTimes(int retryTimes) {
            rxHttp.retryTimes = retryTimes;
            return this;
        }

        /**
         * 设置SSL选项
         */
        public Builder setSslConfig(SSLConfig sslConfig) {
            rxHttp.sslConfig = sslConfig;
            return this;
        }

        /**
         * 设置Gson实例
         */
        public Builder setGson(Gson gson) {
            rxHttp.gson = gson;
            return this;
        }

        /**
         * 设置User-Agent参数
         */
        public Builder setUserAgent(String userAgent) {
            rxHttp.userAgent = userAgent;
            return this;
        }

        /**
         * 同步服务器时间
         */
        public Builder syncServerTime() {
            rxHttp.syncServerTime = true;
            return this;
        }

        /**
         * 创建RxHttp实例
         */
        public RxHttp create() {
            if (rxHttp.gson == null) {
                rxHttp.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setDateFormat(rxHttp.dateFormat).create();
            }
            return rxHttp;
        }
    }

    public static RxHttp.Builder newBuilder() {
        return new RxHttp.Builder();
    }

    /*时间格式*/
    private String dateFormat = defaultDateFormat;
    /*重试次数*/
    private int retryTimes = defaultRetryTimes;
    /*ssl选项*/
    private SSLConfig sslConfig = defaultSSLConfig;
    /*gson*/
    private Gson gson;
    /*User-Agent*/
    private String userAgent = defaultUserAgent;
    private boolean syncServerTime;

    private RxHttp.HttpApi create(String url) {
        HttpLoggingInterceptor.Level logLevel = debug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(OkHttp3Utils.userAgentInterceptor(userAgent))
                .addInterceptor(OkHttp3Utils.newLogger(logLevel))
                .sslSocketFactory(sslConfig.getSslSocketFactory(), sslConfig.getX509TrustManager())
                .hostnameVerifier(sslConfig.getHostnameVerifier())
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        if (syncServerTime) {
            builder.addNetworkInterceptor(ServerTime.interceptor);
        }
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(builder.build())
                .addConverterFactory(DateConverterFactory.create(dateFormat))
                .addCallAdapterFactory(RxJava2RetryCallAdapterFactory.create(retryTimes, Schedulers.newThread()))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(RxHttp.HttpApi.class);
    }

    /**
     * GET请求
     */
    public Observable<String> get(String url) {
        return get0(url, EMPTY_HEADERS, String.class);
    }

    /**
     * GET请求
     *
     * @param params 请求参数
     */
    public Observable<String> get(String url, String params) {
        return get0(appendURL(url, params), EMPTY_HEADERS, String.class);
    }

    /**
     * GET请求
     *
     * @param params 请求参数
     */
    public Observable<String> get(String url, Map<String, String> params) {
        return get0(appendURL(url, map2string(params)), EMPTY_HEADERS, String.class);
    }

    /**
     * GET请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public Observable<String> get(String url, String params, Map<String, String> headers) {
        return get0(appendURL(url, params), headers, String.class);
    }

    /**
     * GET请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public Observable<String> get(String url, Map<String, String> params, Map<String, String> headers) {
        return get0(appendURL(url, map2string(params)), headers, String.class);
    }

    /**
     * GET请求
     */
    public <T> Observable<T> get(String url, Class<T> type) {
        return get0(url, EMPTY_HEADERS, type);
    }

    /**
     * GET请求
     *
     * @param params 请求参数
     */
    public <T> Observable<T> get(String url, String params, Class<T> type) {
        return get0(appendURL(url, params), EMPTY_HEADERS, type);
    }

    /**
     * GET请求
     *
     * @param params 请求参数
     */
    public <T> Observable<T> get(String url, Map<String, String> params, Class<T> type) {
        return get0(appendURL(url, map2string(params)), EMPTY_HEADERS, type);
    }

    /**
     * GET请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public <T> Observable<T> get(String url, String params, Map<String, String> headers, Class<T> type) {
        return get0(appendURL(url, params), headers, type);
    }

    /**
     * GET请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public <T> Observable<T> get(String url, Map<String, String> params, Map<String, String> headers, Class<T> type) {
        return get0(appendURL(url, map2string(params)), headers, type);
    }

    private <T> Observable<T> get0(String url, Map<String, String> headers, Class<T> type) {
        String[] array = splitURL(url, true);
        url = array[0];
        String path = array[1];
        String params = array[2];
        return create(url).get(path, headers, string2map(params))
                .map(resp -> resp2obj(resp, type));
    }

    /**
     * POST请求
     */
    public Observable<String> post(String url) {
        return post0(url, EMPTY_HEADERS, String.class);
    }

    /**
     * POST请求
     *
     * @param params 请求参数
     */
    public Observable<String> post(String url, String params) {
        return post0(appendURL(url, params), EMPTY_HEADERS, String.class);
    }

    /**
     * POST请求
     *
     * @param params 请求参数
     */
    public Observable<String> post(String url, Map<String, String> params) {
        return post0(appendURL(url, map2string(params)), EMPTY_HEADERS, String.class);
    }

    /**
     * POST请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public Observable<String> post(String url, String params, Map<String, String> headers) {
        return post0(appendURL(url, params), headers, String.class);
    }

    /**
     * POST请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public Observable<String> post(String url, Map<String, String> params, Map<String, String> headers) {
        return post0(appendURL(url, map2string(params)), headers, String.class);
    }

    /**
     * POST请求
     */
    public <T> Observable<T> post(String url, Class<T> type) {
        return post0(url, EMPTY_HEADERS, type);
    }

    /**
     * POST请求
     *
     * @param params 请求参数
     */
    public <T> Observable<T> post(String url, String params, Class<T> type) {
        return post0(appendURL(url, params), EMPTY_HEADERS, type);
    }

    /**
     * POST请求
     *
     * @param params 请求参数
     */
    public <T> Observable<T> post(String url, Map<String, String> params, Class<T> type) {
        return post0(appendURL(url, map2string(params)), EMPTY_HEADERS, type);
    }

    /**
     * POST请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public <T> Observable<T> post(String url, String params, Map<String, String> headers, Class<T> type) {
        return post0(appendURL(url, params), headers, type);
    }

    /**
     * POST请求
     *
     * @param params  请求参数
     * @param headers 请求头
     */
    public <T> Observable<T> post(String url, Map<String, String> params, Map<String, String> headers, Class<T> type) {
        return post0(appendURL(url, map2string(params)), headers, type);
    }

    private <T> Observable<T> post0(String url, Map<String, String> headers, Class<T> type) {
        String[] array = splitURL(url, true);
        url = array[0];
        String path = array[1];
        String params = array[2];
        RequestBody body = RequestBody.create(OkHttp3Utils.application_x_www_form_urlencoded, StringUtils.getBytesUtf8(params));
        return create(url).post(path, headers, body)
                .map(resp -> resp2obj(resp, type));
    }

    /**
     * POST请求
     *
     * @param json Json格式的请求参数
     */
    public Observable<String> postJson(String url, Object json) {
        return postJson(url, json, EMPTY_HEADERS, String.class);
    }

    /**
     * POST请求
     *
     * @param json    Json格式的请求参数
     * @param headers 请求头
     */
    public Observable<String> postJson(String url, Object json, Map<String, String> headers) {
        return postJson(url, json, headers, String.class);
    }

    /**
     * POST请求
     *
     * @param json Json格式的请求参数
     */
    public <T> Observable<T> postJson(String url, Object json, Class<T> type) {
        return postJson(url, json, EMPTY_HEADERS, type);
    }

    public <T> Observable<T> postJson(String url, Object json, Map<String, String> headers, Class<T> type) {
        String[] array = splitURL(url, false);
        url = array[0];
        String path = array[1];
        String string = json instanceof String ? (String) json : gson.toJson(json);
        byte[] data = StringUtils.getBytesUtf8(string);
        RequestBody body = RequestBody.create(OkHttp3Utils.application_json, data);
        return create(url).post(path, headers, body)
                .map(resp -> resp2obj(resp, type));
    }

    /**
     * POST请求
     *
     * @param data 请求参数
     */
    public Observable<String> post(String url, byte[] data) {
        return post(url, data, EMPTY_HEADERS, String.class);
    }

    /**
     * POST请求
     *
     * @param data    请求参数
     * @param headers 请求头
     */
    public Observable<String> post(String url, byte[] data, Map<String, String> headers) {
        return post(url, data, headers, String.class);
    }

    /**
     * POST请求
     *
     * @param data 请求参数
     */
    public <T> Observable<T> post(String url, byte[] data, Class<T> type) {
        return post(url, data, EMPTY_HEADERS, type);
    }

    /**
     * POST请求
     *
     * @param data 请求参数
     */
    public <T> Observable<T> post(String url, byte[] data, Map<String, String> headers, Class<T> type) {
        String[] array = splitURL(url, false);
        url = array[0];
        String path = array[1];
        RequestBody body = RequestBody.create(OkHttp3Utils.application_octet_stream, data);
        return create(url).post(path, headers, body)
                .map(resp -> resp2obj(resp, type));
    }

    /**
     * POST请求
     *
     * @param multiParts 请求参数
     */
    public Observable<String> postMultiParts(String url, Map<String, Object> multiParts) {
        return postMultiParts(url, multiParts, EMPTY_HEADERS, String.class);
    }

    /**
     * POST请求
     *
     * @param multiParts 请求参数
     * @param headers    请求头
     */
    public Observable<String> postMultiParts(String url, Map<String, Object> multiParts, Map<String, String> headers) {
        return postMultiParts(url, multiParts, headers, String.class);
    }

    /**
     * POST请求
     *
     * @param multiParts 请求参数
     */
    public <T> Observable<T> postMultiParts(String url, Map<String, Object> multiParts, Class<T> type) {
        return postMultiParts(url, multiParts, EMPTY_HEADERS, type);
    }

    /**
     * POST请求
     *
     * @param multiParts 请求参数
     * @param headers    请求头
     */
    public <T> Observable<T> postMultiParts(String url, Map<String, Object> multiParts, Map<String, String> headers, Class<T> type) {
        String[] array = splitURL(url, false);
        url = array[0];
        String path = array[1];
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Map.Entry<String, Object> e : multiParts.entrySet()) {
            final String name = e.getKey();
            final Object value = e.getValue();
            if (value == null) {
                continue;
            } else if (value instanceof RequestBody) {
                builder.addFormDataPart(name, null, RequestBody.class.cast(value));
            } else if (value instanceof File) {
                File file = File.class.cast(value);
                builder.addFormDataPart(name, file.getName(), OkHttp3Utils.toRequestBody(file));
            } else if (value instanceof MockFile) {
                MockFile file = MockFile.class.cast(value);
                builder.addFormDataPart(name, file.getName(), OkHttp3Utils.toRequestBody(file));
            } else if (value instanceof byte[]) {
                builder.addFormDataPart(name, null, OkHttp3Utils.toRequestBody(byte[].class.cast(value)));
            } else if (value instanceof CharSequence) {
                String string = value.toString();
                builder.addFormDataPart(name, string);
            } else if (value instanceof Long
                    || value instanceof Integer
                    || value instanceof Short
                    || value instanceof Byte
                    || value instanceof Double
                    || value instanceof Float) {
                builder.addFormDataPart(name, value.toString());
            } else {
                throw new IllegalArgumentException("");
            }
        }
        return create(url).post(path, headers, builder.build())
                .map(resp -> resp2obj(resp, type));
    }

    /**
     * 对象转JSON格式字符串
     */
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * JSON格式字符串转对象
     */
    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public <T> Map<String, T> toMap(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    private static String[] splitURL(String url, boolean parseParams) {
        List<String> array1 = split(url, "\\?");
        String params = array1.size() > 1 ? array1.get(1) : "";
        url = array1.get(0);
        int index = url.indexOf('/', 8);
        String path;
        if (index < 0) {
            path = "";
        } else {
            index = url.lastIndexOf('/');
            path = url.substring(index + 1);
            url = url.substring(0, index + 1);
        }
        if (!parseParams) {
            if (!Strings.isNullOrEmpty(params)) {
                path = path + "?" + params;
            }
            return new String[]{url, path};
        } else {
            return new String[]{url, path, params};
        }
    }

    /**
     * URL拼接
     */
    public static String appendURL(String url, String param) {
        if (!Strings.isNullOrEmpty(param)) {
            if (url.indexOf('?') < 0) {
                url += '?' + param;
            } else {
                url += '&' + param;
            }
        }
        return url;
    }

    private <T> T resp2obj(ResponseBody resp, Class<T> type) throws IOException {
        if (type == ResponseBody.class) {
            return type.cast(resp);
        }
        if (type == String.class) {
            return type.cast(resp.string());
        }
        if (type == byte[].class) {
            return type.cast(resp.bytes());
        }
        if (type == InputStream.class) {
            return type.cast(resp.byteStream());
        }
        if (type == Reader.class) {
            return type.cast(resp.charStream());
        }
        if (type == BufferedSource.class) {
            return type.cast(resp.source());
        }
        return fromJson(resp.string(), type);
    }

    /**
     * Map对象转为key=value&amp;key2=value2...格式
     */
    public static String map2string(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!first) {
                sb.append('&');
            }
            first = false;
            sb.append(urlEncode(e.getKey()));
            sb.append('=');
            sb.append(urlEncode(Strings.nullToEmpty(e.getValue())));
        }
        return sb.toString();
    }

    /**
     * key=value&amp;key2=value2...格式的字符串转Map对象
     */
    public static Map<String, String> string2map(String string) {
        Map<String, String> params = Maps.newLinkedHashMap();
        if (!Strings.isNullOrEmpty(string)) {
            for (String s : split(string, "&")) {
                List<String> array2 = split(s, "=");
                String key = array2.get(0);
                String value = array2.size() > 1 ? array2.get(1) : "";
                params.put(urlDecode(key), urlDecode(value));
            }
        }
        return params;
    }

    /**
     * URLEncode
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return URLEncoder.encode(s);
        }
    }

    /**
     * URLDecode
     */
    public static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return URLDecoder.decode(s);
        }
    }

    private static List<String> split(String text, String expression) {
        if (Strings.isNullOrEmpty(text)) {
            return Lists.newArrayList();
        } else {
            return Lists.newArrayList(text.split(expression, -1));
        }
    }

    public interface HttpApi {
        /**
         * GET请求
         *
         * @param headers 请求头
         * @param params  请求参数
         */
        @GET("{path}")
        Observable<ResponseBody> get(@Path("path") String path, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

        /**
         * POST请求
         *
         * @param headers 请求头
         * @param body    二进制请求内容
         */
        @POST("{path}")
        Observable<ResponseBody> post(@Path("path") String path, @HeaderMap Map<String, String> headers, @Body RequestBody body);
    }
}
