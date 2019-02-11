package com.hzy.weex.frame.weex.module;

import com.hzy.weex.frame.weex.module.component.RichText;
import com.hzy.weex.frame.weex.module.imgpicker.ImagePickerModule;
import com.hzy.weex.frame.weex.module.location.GeolocationModule;
import com.taobao.weex.WXSDKEngine;

public class WXModuleManager {

    public static void initialize() {
        try {
            WXSDKEngine.registerComponent("richtext", RichText.class);
            WXSDKEngine.registerModule("geolocation", GeolocationModule.class);
            WXSDKEngine.registerModule("imagePicker", ImagePickerModule.class);
            // 覆盖原来的WXNavigatorModule, 防止intent category和别人的一样
            WXSDKEngine.registerModule("navigator", ExNavigatorModule.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
