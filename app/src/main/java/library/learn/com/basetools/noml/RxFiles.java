package library.learn.com.basetools.noml;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.RandomAccessFile;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * File工具类
 * Created by tianyingyingjie on 2017/11/29.
 */
public class RxFiles {
    /**
     * 读取文件的部分内容
     *
     * @param file     文件
     * @param position 起始未知
     * @param size     字节长度
     * @return 指定部分的文件内容
     */
    public static Observable<byte[]> toByteArray(File file, long position, int size) {
        return Observable.fromCallable(() -> {
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");
            try {
                accessFile.seek(position);
                final int count = (int) Math.min(size, accessFile.length() - position);
                byte[] array = new byte[count];
                accessFile.readFully(array);
                return array;
            } finally {
                IOUtils.closeQuietly(accessFile);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 读取文件的部分内容
     *
     * @param file 文件
     * @return 文件内容
     */
    public static Observable<byte[]> toByteArray(File file) {
        return Observable.fromCallable(
                () -> org.apache.commons.io.FileUtils.readFileToByteArray(file)
        ).subscribeOn(Schedulers.io());
    }
}
