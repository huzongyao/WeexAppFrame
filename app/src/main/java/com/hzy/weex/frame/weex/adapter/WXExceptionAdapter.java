package com.hzy.weex.frame.weex.adapter;

import com.taobao.weex.WXEnvironment;
import com.taobao.weex.adapter.IWXJSExceptionAdapter;
import com.taobao.weex.common.WXJSExceptionInfo;
import com.taobao.weex.utils.WXLogUtils;

public class WXExceptionAdapter implements IWXJSExceptionAdapter {

    @Override
    public void onJSException(WXJSExceptionInfo exception) {
        if (exception != null && WXEnvironment.isApkDebugable()) {
            WXLogUtils.e(exception.toString());
        }
    }
}
