package library.learn.com.basetools.noml;

import java.util.Calendar;
import java.util.TimeZone;

import lombok.Setter;

/**
 * 日期工具类
 * Created by jay on 2017/11/29.
 */

public class CalendarUtils {
    @Setter
    private static TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

    /**
     * 获取一个日期实例
     */
    public static Calendar getCalendar() {
        return Calendar.getInstance(timeZone);
    }

    /**
     * 设置小时、分钟、秒钟参数
     */
    public static Calendar setHHmmss(Calendar cal, int hour, int minute, int second) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
