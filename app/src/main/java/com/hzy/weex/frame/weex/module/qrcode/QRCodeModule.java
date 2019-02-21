package com.hzy.weex.frame.weex.module.qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hzy.weex.frame.activity.QRScanActivity;
import com.hzy.weex.frame.constant.AppConfig;
import com.hzy.weex.frame.constant.RequestCode;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.Map;

public class QRCodeModule extends WXModule {

    private Map<String, Object> mScanOptions;
    private JSCallback mCallback;

    @JSMethod
    @SuppressWarnings("unused")
    public boolean startScan(Map<String, Object> options, final JSCallback callback) {
        Context context = mWXSDKInstance.getContext();
        if (!(context instanceof Activity)) {
            return false;
        }
        try {
            mScanOptions = options;
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, QRScanActivity.class);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(intent, RequestCode.REQUEST_CODE_MODULE_QR_SCAN);
                mCallback = callback;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_MODULE_QR_SCAN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String content = data.getStringExtra(QRScanActivity.EXTRA_CONTENT);
                if (!StringUtils.isTrimEmpty(content)) {
                    if (AppConfig.DEBUG) {
                        ToastUtils.showShort(content);
                    }
                    JSONObject result = new JSONObject();
                    result.put("content", content);
                    if (mCallback != null) {
                        mCallback.invoke(result);
                    }
                    return;
                }
            }
            if (mCallback != null) {
                mCallback.invoke(null);
            }
        }
    }
}
