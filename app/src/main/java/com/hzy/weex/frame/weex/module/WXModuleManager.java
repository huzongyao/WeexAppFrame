package com.hzy.weex.frame.weex.module;

import com.hzy.weex.frame.weex.image.FrescoImage;
import com.hzy.weex.frame.weex.module.device.ExDeviceInfoModule;
import com.hzy.weex.frame.weex.module.imgpicker.ImagePickerModule;
import com.hzy.weex.frame.weex.module.location.LocationModule;
import com.hzy.weex.frame.weex.module.prefrence.SharedPreferenceModule;
import com.hzy.weex.frame.weex.module.qrcode.QRCodeModule;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.ui.SimpleComponentHolder;
import com.taobao.weex.ui.component.WXBasicComponentType;

public class WXModuleManager {

    public static void initialize() {
        try {
            WXSDKEngine.registerModule("imagePicker", ImagePickerModule.class);
            // 覆盖原来的WXNavigatorModule, 防止intent category和别人的一样
            WXSDKEngine.registerModule("navigator", ExNavigatorModule.class);
            // 二维码相关模块
            WXSDKEngine.registerModule("qrCode", QRCodeModule.class);
            // SharedPreference相关模块
            WXSDKEngine.registerModule("preference", SharedPreferenceModule.class);
            // 定位模块
            WXSDKEngine.registerModule("location", LocationModule.class);
            // Fresco图片加载
            WXSDKEngine.registerComponent(new SimpleComponentHolder(FrescoImage.class, new FrescoImage.Creator()),
                    false,
                    WXBasicComponentType.IMAGE,
                    WXBasicComponentType.IMG);
            WXSDKEngine.registerModule("deviceInfo", ExDeviceInfoModule.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
