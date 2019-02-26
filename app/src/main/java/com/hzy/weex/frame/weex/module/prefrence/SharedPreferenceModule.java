package com.hzy.weex.frame.weex.module.prefrence;

import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

public class SharedPreferenceModule extends WXModule {

    private final SPUtils mSPUtils;

    public SharedPreferenceModule() {
        mSPUtils = SPUtils.getInstance("wxPreference", Context.MODE_PRIVATE);
    }

    @JSMethod
    @SuppressWarnings("unused")
    public void put(String key, String value) {
        mSPUtils.put(key, value);
    }

    @JSMethod
    @SuppressWarnings("unused")
    public String get(String key) {
        return mSPUtils.getString(key);
    }

    @JSMethod
    @SuppressWarnings("unused")
    public void clear() {
        mSPUtils.clear();
    }

    @JSMethod
    @SuppressWarnings("unused")
    public void remove(String key) {
        mSPUtils.remove(key);
    }
}
