package library.learn.com.basetools.view;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog.Builder;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.Observable;

/**
 * Created by jay on 2017/11/29.
 */

public class BaseActivity extends RxAppCompatActivity {
    public BaseActivity() {
    }

    public final void startActivity(Class<? extends Activity> activity) {
        this.startActivity(activity, false);
    }

    public final void startActivity(Class<? extends Activity> activity, boolean finish) {
        this.startActivity(new Intent(this, activity));
        if (finish) {
            this.finish();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RxActivity.onActivityResult(this, requestCode, resultCode, data);
    }

    public final Observable<Boolean> requestPermissions(String... permissions) {
        return (new RxPermissions(this)).request(permissions).compose(this.bindToLifecycle());
    }

    // TODO: 2017/11/29 设置  取笑
    public void showAppSettingsDialog(String message, boolean finishIfCancel) {
        Builder builder = new Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setCancelable(false);
//        builder.setNegativeButton("取消", BaseActivity$$Lambda$1.lambdaFactory$(this, finishIfCancel));
//        builder.setPositiveButton("设置", BaseActivity$$Lambda$2.lambdaFactory$(this));
        builder.create().show();
    }

    public final void openAppSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        this.startActivity(intent);
    }
}