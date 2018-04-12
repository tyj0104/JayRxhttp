package library.learn.com.basetools.view;


import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;


/**
 * Created by jay on 2017/11/29.
 */
public class App extends Application {
    private static App instance;

    private boolean multiDexEnabled;

    public App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        ToastUtils.init(this);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ToastUtils.init(this);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
    }

    public static App getInstance() {
        return instance;
    }



    protected void setMultiDexEnabled(boolean multiDexEnabled) {
        this.multiDexEnabled = multiDexEnabled;
    }
}
