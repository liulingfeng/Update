package com.llf.update;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.btn)
    Button btn;

    private static final String APP_NAME = "Ped_android";
    private static final String INFO_NAME = "计步器";
    private static final String VERSION = "1.0.0";
    private static final String STORE_APK = "dingding_apk";

    private UpdateUtil.UpdateCallback mUpdateCallback; // 更新回调
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUtil.checkUpdate(APP_NAME, VERSION, mUpdateCallback);
            }
        });

        mUpdateCallback = new UpdateUtil.UpdateCallback() {
            @Override
            public void onSuccess(InfoEntity info) {
                Toast.makeText(MainActivity.this, "有更新", Toast.LENGTH_SHORT).show();
                UpdateUtil.downloadApk(MainActivity.this, info, INFO_NAME, STORE_APK);
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
