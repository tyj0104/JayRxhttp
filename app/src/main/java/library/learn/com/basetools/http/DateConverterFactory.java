package library.learn.com.basetools.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

/**
 * Retrofit2日期转换工厂
 * Created by jay on 2017/1/19.
 */

@AllArgsConstructor
public final class DateConverterFactory extends Factory {
    /**
     * 指定日期格式创建实例
     */
    public static Factory create(String format) {
        return new DateConverterFactory(new SimpleDateFormat(format));
    }

    /**
     * 日期格式
     */
    private final DateFormat dateFormat;

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type != Date.class) return null;
        return f -> f == null ? "" : dateFormat.format((Date) f);
    }
}
