package library.learn.com.basetools.view;

/**
 * Created by jay on 2017/11/29.
 */

import android.app.Activity;

public abstract class BackgroundWatcher extends ActivityLifecycleAdapter {
    private int count = 0;

    public BackgroundWatcher() {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(this.filter(activity) && --this.count == 0) {
            this.applicationDidEnterBackground(activity);
        }

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if(this.filter(activity) && ++this.count == 1) {
            this.applicationWillEnterForeground(activity);
        }

    }

    protected boolean filter(Activity activity) {
        return true;
    }

    protected abstract void applicationDidEnterBackground(Activity var1);

    protected abstract void applicationWillEnterForeground(Activity var1);
}
