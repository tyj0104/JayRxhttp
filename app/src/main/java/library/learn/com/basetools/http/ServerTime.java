package library.learn.com.basetools.http;

import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 服务器时间同步类
 * Created by jay on 2017/7/24.
 */

public class ServerTime {
    private static long gapMillis;

    static synchronized void setServerTime(Date serverTime) {
        long localTime = System.currentTimeMillis();
        gapMillis = serverTime.getTime() - localTime;
    }

    public static synchronized Date getServerTime() {
        long localTime = System.currentTimeMillis();
        return new Date(localTime + gapMillis);
    }

    static final Interceptor interceptor = chain -> {
        Response resp = chain.proceed(chain.request());
        Date serverTime = resp.headers().getDate("Date");
        ServerTime.setServerTime(serverTime);
        return resp;
    };
}
