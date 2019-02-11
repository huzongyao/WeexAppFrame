package com.hzy.weex.frame;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.hzy.weex.frame.constant.AppConfig;
import com.hzy.weex.frame.weex.adapter.GlideImageAdapter;
import com.hzy.weex.frame.weex.adapter.OkWSAdapterFactory;
import com.hzy.weex.frame.weex.adapter.OkWXHttpAdapter;
import com.hzy.weex.frame.weex.adapter.WXExceptionAdapter;
import com.hzy.weex.frame.weex.module.WXModuleManager;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initAndroidUtils();
        initARouter();
        initWXSdk();
    }

    private void initAndroidUtils() {
        Utils.init(this);
        LogUtils.getConfig().setLogSwitch(AppConfig.DEBUG)
                .setBorderSwitch(false)
                .setLogHeadSwitch(false);
    }

    private void initARouter() {
        if (AppConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    private void initWXSdk() {
        InitConfig config = new InitConfig.Builder()
                .setImgAdapter(new GlideImageAdapter())
                .setJSExceptionAdapter(new WXExceptionAdapter())
                .setHttpAdapter(new OkWXHttpAdapter())
                .setWebSocketAdapterFactory(new OkWSAdapterFactory())
                .build();
        WXSDKEngine.initialize(this, config);
        WXModuleManager.initialize();
    }
}
