package com.hzy.weex.frame.weex.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.taobao.weex.WXSDKInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class WXAnalyzerDelegate {
    private static boolean ENABLE = false;
    private Object mWXAnalyzer;

    @SuppressWarnings("unchecked")
    public WXAnalyzerDelegate(@Nullable Context context) {
        if (!ENABLE) {
            return;
        }
        if (context == null) {
            return;
        }
        try {
            Class clazz = Class.forName("com.taobao.weex.analyzer.WeexDevOptions");
            Constructor constructor = clazz.getDeclaredConstructor(Context.class);
            mWXAnalyzer = constructor.newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {
        if (mWXAnalyzer == null) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass().getDeclaredMethod("onCreate");
            method.invoke(mWXAnalyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onStart() {
        if (mWXAnalyzer == null) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass().getDeclaredMethod("onStart");
            method.invoke(mWXAnalyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        if (mWXAnalyzer == null) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass().getDeclaredMethod("onResume");
            method.invoke(mWXAnalyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onPause() {
        if (mWXAnalyzer == null) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass().getDeclaredMethod("onPause");
            method.invoke(mWXAnalyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        if (mWXAnalyzer == null) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass().getDeclaredMethod("onStop");
            method.invoke(mWXAnalyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (mWXAnalyzer == null) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass().getDeclaredMethod("onDestroy");
            method.invoke(mWXAnalyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onWeexRenderSuccess(@Nullable WXSDKInstance instance) {
        if (mWXAnalyzer == null || instance == null) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass()
                    .getDeclaredMethod("onWeexRenderSuccess", WXSDKInstance.class);
            method.invoke(mWXAnalyzer, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mWXAnalyzer == null) {
            return false;
        }
        try {
            Method method = mWXAnalyzer.getClass()
                    .getDeclaredMethod("onKeyUp", int.class, KeyEvent.class);
            return (boolean) method.invoke(mWXAnalyzer, keyCode, event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onException(WXSDKInstance instance, String errCode, String msg) {
        if (mWXAnalyzer == null) {
            return;
        }
        if (TextUtils.isEmpty(errCode) && TextUtils.isEmpty(msg)) {
            return;
        }
        try {
            Method method = mWXAnalyzer.getClass()
                    .getDeclaredMethod("onException", WXSDKInstance.class, String.class, String.class);
            method.invoke(mWXAnalyzer, instance, errCode, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View onWeexViewCreated(WXSDKInstance instance, View view) {
        if (mWXAnalyzer == null || instance == null || view == null) {
            return null;
        }
        try {
            Method method = mWXAnalyzer.getClass()
                    .getDeclaredMethod("onWeexViewCreated", WXSDKInstance.class, View.class);
            View retView = (View) method.invoke(mWXAnalyzer, instance, view);
            return retView;
        } catch (Exception e) {
            e.printStackTrace();
            return view;
        }
    }

}
