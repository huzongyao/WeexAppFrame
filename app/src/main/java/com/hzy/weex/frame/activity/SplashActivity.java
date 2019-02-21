package com.hzy.weex.frame.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hzy.weex.frame.R;
import com.hzy.weex.frame.constant.RouterHub;
import com.hzy.weex.frame.widget.AutoWidthImageView;

@Route(path = RouterHub.SPLASH_ACTIVITY)
public class SplashActivity extends AppCompatActivity {

    private static boolean isSplashPlayed = false;
    private AutoWidthImageView mBackImage;
    private TextView mSplashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isSplashPlayed) {
            startWxPageAndFinish();
        } else {
            setContentView(R.layout.activity_splash);
            mBackImage = findViewById(R.id.image_back);
            mSplashText = findViewById(R.id.splash_text);
            startWXPageDelay();
            isSplashPlayed = true;
        }
    }

    private void startWXPageDelay() {
        mSplashText.setAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_text_anim));
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_back_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startWxPageAndFinish();
            }
        });
        mBackImage.setAnimation(animation);
    }

    private void startWxPageAndFinish() {
        ARouter.getInstance().build(RouterHub.WX_PAGE_ACTIVITY).navigation();
        finish();
    }
}
