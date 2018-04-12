package library.learn.com.basetools.view;


import android.app.Activity;
import android.os.Bundle;

import java.util.Stack;
/**
 * Created by jay on 2017/11/29.
 */

public final class ActivityManager extends ActivityLifecycleAdapter {
    private static final ActivityManager instance = new ActivityManager();
    private Stack<Activity> activities = new Stack();


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.activities.push(activity);

    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
        this.activities.remove(activity);
    }
    @Override
    public void onActivityResumed(Activity activity) {
        this.activities.remove(activity);
        this.activities.push(activity);
    }

    public <T extends Activity> T getTopActivity() {
        return this.activities.isEmpty()?null: (T) this.activities.peek();
    }

    public void finish(Class<? extends Activity> activity) {
        Activity[] array = (Activity[])this.activities.toArray(new Activity[0]);
        Activity[] var3 = array;
        int var4 = array.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Activity activity2 = var3[var5];
            if(activity2.getClass().equals(activity)) {
                activity2.finish();
            }
        }

    }

    public void finishAll() {
        Activity[] array = (Activity[])this.activities.toArray(new Activity[0]);
        this.activities.clear();
        Activity[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Activity activity = var2[var4];
            activity.finish();
        }

    }

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        return instance;
    }
}
