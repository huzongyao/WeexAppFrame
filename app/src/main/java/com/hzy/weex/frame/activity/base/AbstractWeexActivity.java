package com.hzy.weex.frame.activity.base;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.hzy.weex.frame.weex.adapter.WXAnalyzerDelegate;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.Constants;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWeexActivity extends AppCompatActivity
        implements IWXRenderListener {

    private static final String TAG = "AbstractWeexActivity";
    protected WXAnalyzerDelegate mWxAnalyzerDelegate;
    private ViewGroup mContainer;
    private WXSDKInstance mInstance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createWeexInstance();
        mInstance.onActivityCreate();
        mWxAnalyzerDelegate = new WXAnalyzerDelegate(this);
        mWxAnalyzerDelegate.onCreate();
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    protected final ViewGroup getContainer() {
        return mContainer;
    }

    protected final void setContainer(ViewGroup container) {
        mContainer = container;
    }

    protected void destroyWeexInstance() {
        if (mInstance != null) {
            mInstance.registerRenderListener(null);
            mInstance.destroy();
            mInstance = null;
        }
    }

    protected void createWeexInstance() {
        destroyWeexInstance();

        Rect outRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);

        mInstance = new WXSDKInstance(this);
        mInstance.registerRenderListener(this);
    }

    protected void renderPage(String template, String source) {
        renderPage(template, source, null);
    }

    protected void renderPage(String template, String source, String jsonInitData) {
        if (mContainer != null) {
            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, source);
            // Set options.bundleDigest
            try {
                String banner = WXUtils.getBundleBanner(template);
                JSONObject jsonObj = JSONObject.parseObject(banner);
                String digest = null;
                if (jsonObj != null) {
                    digest = jsonObj.getString(Constants.CodeCache.BANNER_DIGEST);
                }
                if (digest != null) {
                    options.put(Constants.CodeCache.DIGEST, digest);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            //Set options.codeCachePath
            String path = WXEnvironment.getFilesDir(getApplicationContext());
            path += File.separator;
            path += Constants.CodeCache.SAVE_PATH;
            path += File.separator;
            options.put(Constants.CodeCache.PATH, path);

            mInstance.setTrackComponent(true);
            mInstance.render(getPageName(), template, options, jsonInitData,
                    WXRenderStrategy.APPEND_ASYNC);
        }
    }

    protected void renderPageByURL(String url) {
        renderPageByURL(url, null);
    }

    protected void renderPageByURL(String url, String jsonInitData) {
        if (mContainer != null) {
            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, url);
            mInstance.setTrackComponent(true);
            mInstance.renderByUrl(getPageName(), url, options, jsonInitData,
                    WXRenderStrategy.APPEND_ASYNC);
        }
    }

    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mInstance != null) {
            mInstance.onActivityStart();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInstance != null) {
            mInstance.onActivityResume();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mInstance != null) {
            mInstance.onActivityPause();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mInstance != null) {
            mInstance.onActivityStop();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mInstance != null) {
            mInstance.onActivityDestroy();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onDestroy();
        }
    }

    @Override
    public void onViewCreated(WXSDKInstance wxsdkInstance, View view) {
        View wrappedView = null;
        if (mWxAnalyzerDelegate != null) {
            wrappedView = mWxAnalyzerDelegate.onWeexViewCreated(wxsdkInstance, view);
        }
        if (wrappedView != null) {
            view = wrappedView;
        }
        if (mContainer != null) {
            mContainer.removeAllViews();
            mContainer.addView(view);
        }
    }


    @Override
    public void onRefreshSuccess(WXSDKInstance wxsdkInstance, int i, int i1) {

    }

    @Override
    @CallSuper
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onWeexRenderSuccess(instance);
        }
    }

    @Override
    @CallSuper
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onException(instance, errCode, msg);
        }
    }

    @Override
    @CallSuper
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return (mWxAnalyzerDelegate != null && mWxAnalyzerDelegate.onKeyUp(keyCode, event))
                || super.onKeyUp(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mInstance != null) {
            mInstance.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
