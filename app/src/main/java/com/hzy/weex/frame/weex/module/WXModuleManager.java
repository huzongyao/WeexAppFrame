package com.hzy.weex.frame.weex.module;

import com.taobao.weex.WXSDKEngine;

public class WXModuleManager {

    public static void init() {
        try {
            WXSDKEngine.registerComponent("richtext", RichText.class);
            WXSDKEngine.registerModule("geolocation", GeolocationModule.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
