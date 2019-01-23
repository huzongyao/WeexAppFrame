package com.hzy.weex.frame;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
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
        initWXSdk();
    }

    private void initAndroidUtils() {
        Utils.init(this);
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG)
                .setBorderSwitch(false)
                .setLogHeadSwitch(false);
    }

    private void initWXSdk() {
        InitConfig config = new InitConfig.Builder()
                .setImgAdapter(new GlideImageAdapter())
                .setJSExceptionAdapter(new WXExceptionAdapter())
                .setHttpAdapter(new OkWXHttpAdapter())
                .setWebSocketAdapterFactory(new OkWSAdapterFactory())
                .build();
        WXSDKEngine.initialize(this, config);
        WXModuleManager.init();
    }
}
