package com.hzy.weex.frame.weex.image;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.ComponentCreator;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXImage;
import com.taobao.weex.ui.component.WXVContainer;

import java.lang.reflect.InvocationTargetException;

public class FrescoImage extends WXImage {

    public FrescoImage(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    @Override
    protected ImageView initComponentHostView(@NonNull Context context) {
        FrescoImageView view = new FrescoImageView(context);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        return view;
    }

    public static class Creator implements ComponentCreator {
        @Override
        public WXComponent createInstance(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return new FrescoImage(instance, parent, basicComponentData);
        }
    }
}
