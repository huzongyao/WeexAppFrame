package com.hzy.weex.frame.weex.module.location;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

public class LocationModule extends WXModule {

    public LocationModule() {
    }

    @JSMethod
    @SuppressWarnings("unused")
    public boolean getLocation(String options, JSCallback callback) {
        PermissionUtils.permission(PermissionConstants.LOCATION)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        LocationInstance.INSTANCE.getLocation(callback);
                    }

                    @Override
                    public void onDenied() {
                        callback.invoke(null);
                    }
                }).request();
        return true;
    }
}
