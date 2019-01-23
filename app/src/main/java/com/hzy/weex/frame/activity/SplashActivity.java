package com.hzy.weex.frame.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.hzy.weex.frame.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 800L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startWXPageDelay();
    }

    private void startWXPageDelay() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ActivityUtils.startActivity(WXPageActivity.class);
                finish();
            }
        }, SPLASH_DELAY);
    }
}
