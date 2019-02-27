package com.hzy.weex.frame.weex.module;

import com.hzy.weex.frame.weex.module.component.RichText;
import com.hzy.weex.frame.weex.module.imgpicker.ImagePickerModule;
import com.hzy.weex.frame.weex.module.location.LocationModule;
import com.hzy.weex.frame.weex.module.prefrence.SharedPreferenceModule;
import com.hzy.weex.frame.weex.module.qrcode.QRCodeModule;
import com.taobao.weex.WXSDKEngine;

public class WXModuleManager {

    public static void initialize() {
        try {
            WXSDKEngine.registerComponent("richtext", RichText.class);
            WXSDKEngine.registerModule("imagePicker", ImagePickerModule.class);
            // 覆盖原来的WXNavigatorModule, 防止intent category和别人的一样
            WXSDKEngine.registerModule("navigator", ExNavigatorModule.class);
            // 二维码相关模块
            WXSDKEngine.registerModule("qrCode", QRCodeModule.class);
            // SharedPreference相关模块
            WXSDKEngine.registerModule("preference", SharedPreferenceModule.class);
            // 定位模块
            WXSDKEngine.registerModule("location", LocationModule.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
