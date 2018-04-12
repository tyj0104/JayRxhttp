package library.learn.com.basetools.noml;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;



import library.learn.com.basetools.R;
import library.learn.com.basetools.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        mBinding =   DataBindingUtil.setContentView(this,R.layout.activity_main);
        iniView();

    }

    private void iniView() {
        mBinding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mBinding.loading.start();
            }
        });

        mBinding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.loading.setVisibility(View.GONE);
            }
        });

    }
}
