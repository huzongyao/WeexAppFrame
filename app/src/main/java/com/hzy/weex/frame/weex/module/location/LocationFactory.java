package com.hzy.weex.frame.weex.module.location;

import com.taobao.weex.WXSDKInstance;

public class LocationFactory {

    public static ILocatable getLocationProvider(WXSDKInstance context) {
        return new DefaultLocation(context);
    }
}
