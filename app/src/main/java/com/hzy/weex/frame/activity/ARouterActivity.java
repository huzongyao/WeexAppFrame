package com.hzy.weex.frame.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hzy.weex.frame.constant.RouterHub;

/**
 * Created by huzongyao on 2019/1/24.
 * 新建一个Activity用于监听Schame事件,之后直接把url传递给ARouter即可
 */
@Route(path = RouterHub.AROUTER_ACTIVITY)
public class ARouterActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null) {
            ARouter.getInstance().build(uri).navigation();
        }
        finish();
    }
}
