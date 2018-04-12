package library.learn.com.basetools.view;


import android.app.Activity;
import android.content.Intent;

import com.google.common.collect.Maps;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.Map;

/**
 * Created by jay on 2017/11/29.
 */

public final class RxActivity {
    private static Map<String, PublishSubject<RxActivity.ActivityResult>> map = Maps.newHashMap();

    public RxActivity() {
    }

    public static Observable<RxActivity.ActivityResult> startActivityForResult(Activity activity, Intent intent, int requestCode) {
        PublishSubject subject = PublishSubject.create();
        map.put(key(activity, requestCode), subject);
        activity.startActivityForResult(intent, requestCode);
        return subject;
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        PublishSubject subject = (PublishSubject) map.remove(key(activity, requestCode));
        if (subject != null) {
            if (data == null) {
                data = new Intent();
            }

            subject.onNext(new RxActivity.ActivityResult(data, requestCode, resultCode));
            subject.onComplete();
        }

    }

    private static String key(Activity activity, int requestCode) {
        return String.format("%d-%d", new Object[]{Integer.valueOf(System.identityHashCode(activity)), Integer.valueOf(requestCode)});
    }

    public static class ActivityResult {
        public final Intent data;
        public final int requestCode;
        public final int resultCode;

        private ActivityResult(Intent data, int requestCode, int resultCode) {
            this.data = data;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }
    }
}