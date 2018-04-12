package library.learn.com.basetools.view;


import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;
/**
 * Created by jay on 2017/11/29.
 */

public class ToastUtils {
    private static Context appContext;
    private static Toast sToast;

    public ToastUtils() {
    }

    public static void init(Context ctx) {
        if(sToast == null) {
            appContext = ctx.getApplicationContext();
            sToast = Toast.makeText(appContext, "", 0);
        }

    }

    public static void toast(String message) {
        sToast.setText(message);
        sToast.show();
    }

    public static void toast(String message, Object... args) {
        toast(String.format(message, args));
    }

    public static void toast(@StringRes int message) {
        toast(appContext.getString(message));
    }

    public static void toast(@StringRes int message, Object... args) {
        toast(appContext.getString(message, args));
    }
}
