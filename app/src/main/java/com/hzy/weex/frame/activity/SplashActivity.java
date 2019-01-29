package com.hzy.weex.frame.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hzy.weex.frame.R;
import com.hzy.weex.frame.constant.RouterHub;

import java.util.Timer;
import java.util.TimerTask;

@Route(path = RouterHub.SPLASH_ACTIVITY)
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 600L;

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
                ARouter.getInstance().build(RouterHub.WX_PAGE_ACTIVITY).navigation();
                finish();
            }
        }, SPLASH_DELAY);
    }
}
