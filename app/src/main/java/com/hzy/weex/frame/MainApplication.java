package com.hzy.weex.frame;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.hzy.weex.frame.constant.AppConfig;
import com.hzy.weex.frame.weex.adapter.FrescoImageAdapter;
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
        OkWXHttpAdapter httpAdapter = new OkWXHttpAdapter();
        InitConfig config = new InitConfig.Builder()
                .setImgAdapter(new FrescoImageAdapter())
                .setJSExceptionAdapter(new WXExceptionAdapter())
                .setHttpAdapter(httpAdapter)
                .setWebSocketAdapterFactory(new OkWSAdapterFactory())
                .build();
        WXSDKEngine.initialize(this, config);
        ImagePipelineConfig frescoConfig = OkHttpImagePipelineConfigFactory
                .newBuilder(this, httpAdapter.getClient()).build();
        Fresco.initialize(this, frescoConfig);
        WXModuleManager.initialize();
    }
}
