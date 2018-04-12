package library.learn.com.basetools.view;

/**
 * Created by jay on 2017/11/29.
 */

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.util.Log;

import lombok.Getter;
import lombok.Setter;

public final class ActivityLifecycleLogger implements ActivityLifecycleCallbacks {
    @Getter
    @Setter
    private final String tag;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.log(activity, "onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.log(activity, "onActivityStarted");
    }
    @Override
    public void onActivityResumed(Activity activity) {
        this.log(activity, "onActivityResumed");
    }
    @Override
    public void onActivityPaused(Activity activity) {
        this.log(activity, "onActivityPaused");
    }
    @Override
    public void onActivityStopped(Activity activity) {
        this.log(activity, "onActivityStopped");
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        this.log(activity, "onActivitySaveInstanceState");
    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        this.log(activity, "onActivityDestroyed");
    }

    private void log(Activity activity, String action) {
        Log.d(this.tag, action + ": " + activity.getClass().getName());
    }


    public ActivityLifecycleLogger(String tag) {
        this.tag = tag;
    }
}
