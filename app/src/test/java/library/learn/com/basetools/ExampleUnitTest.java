package library.learn.com.basetools;


import org.junit.Test;

import library.learn.com.basetools.http.RxHttp;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void Test() {

        RxHttp.getDefaultInstance().post("http://103.28.214.51:1080/pajf_phone_stage/sysgoods/listStage").blockingFirst();

    }
}