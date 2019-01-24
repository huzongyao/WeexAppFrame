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
            WXSDKEngine.registerModule("navigator", ExNavigatorModule.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
