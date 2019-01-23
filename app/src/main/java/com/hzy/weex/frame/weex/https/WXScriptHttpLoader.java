package com.hzy.weex.frame.weex.https;

import android.text.TextUtils;

import com.hzy.weex.frame.event.HttpResultEvent;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXHttpAdapter;
import com.taobao.weex.common.WXRequest;
import com.taobao.weex.common.WXResponse;
import com.taobao.weex.utils.WXLogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

public enum WXScriptHttpLoader {

    INSTANCE;

    private static final String TAG = "WXScriptHttpLoader";

    WXScriptHttpLoader() {
    }

    /**
     * Load script with default WXHttpAdapter
     */
    public void sendRequest(String url) {
        IWXHttpAdapter adapter = WXSDKManager.getInstance().getIWXHttpAdapter();
        WXRequest request = new WXRequest();
        request.url = url;
        request.method = "GET";
        adapter.sendRequest(request, new IWXHttpAdapter.OnHttpListener() {
            @Override
            public void onHttpStart() {
                if (WXEnvironment.isApkDebugable()) {
                    WXLogUtils.d(TAG, "Load Script: [" + url + ']');
                }
            }

            @Override
            public void onHeadersReceived(int statusCode, Map<String, List<String>> headers) {
            }

            @Override
            public void onHttpUploadProgress(int uploadProgress) {
            }

            @Override
            public void onHttpResponseProgress(int loadedLength) {
            }

            @Override
            public void onHttpFinish(WXResponse response) {
                int statusCode = 0;
                if (!TextUtils.isEmpty(response.statusCode)) {
                    try {
                        statusCode = Integer.parseInt(response.statusCode);
                    } catch (NumberFormatException e) {
                        statusCode = 0;
                        WXLogUtils.e(TAG, "IWXHttpAdapter statusCode:" + response.statusCode);
                    }
                }
                if (statusCode >= 200 && statusCode <= 299 && response.originalData != null) {
                    EventBus.getDefault()
                            .post(new HttpResultEvent(HttpResultEvent.HTTP_RESULT_OK, url,
                                    new String(response.originalData)));
                } else {
                    EventBus.getDefault()
                            .post(new HttpResultEvent(HttpResultEvent.HTTP_RESULT_FAIL, url));
                }
            }
        });
    }
}
