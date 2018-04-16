package library.learn.com.basetools.noml;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 工具类
 * Created by jay on 2017/11/29.
 */

public class Utils {
    /**
     * 判断字符串是否为null或空
     */
    public static boolean isNullOrEmpty(String string) {
        return Strings.isNullOrEmpty(string);
    }

    /**
     * 判断集合是否为null或空
     */
    public static boolean isNullOrEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 字符串拼接
     */
    public static String join(CharSequence delimiter, Object[] tokens) {
        if (tokens == null || tokens.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * 字符串拼接
     */
    public static String join(CharSequence delimiter, Iterable tokens) {
        if (tokens == null) return "";
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    /**
     * 字符串分割
     */
    public static List<String> split(String text, String expression) {
        if (isNullOrEmpty(text)) {
            return Lists.newArrayList();
        } else {
            return Lists.newArrayList(text.split(expression, -1));
        }
    }

    /**
     * 字符串分割
     */
    public static List<String> split(String text, Pattern pattern) {
        if (isNullOrEmpty(text)) {
            return Lists.newArrayList();
        } else {
            return Lists.newArrayList(pattern.split(text, -1));
        }
    }

    /**
     * 创建随机文件(UUID格式)
     *
     * @param dir 目录
     */
    public static File newRandomFile(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file;
        do {
            file = new File(dir, UUID.randomUUID().toString());
        } while (file.exists());
        return file;
    }
}
