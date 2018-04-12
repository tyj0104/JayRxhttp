package library.learn.com.basetools.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by jay on 2018/3/12.
 */
@AllArgsConstructor
@Getter
public class MockFile {
    private final String name;
    private final byte[] data;
}
