package com.hzy.weex.frame.weex.module.imgpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.Destroyable;
import com.taobao.weex.common.WXModule;

import java.util.ArrayList;
import java.util.HashMap;

public class ImagePickerModule extends WXModule implements Destroyable {

    private JSCallback mCallback;

    public ImagePickerModule() {
        ImagePicker.getInstance().setCrop(false);
        ImagePicker.getInstance().setImageLoader(new GlideImagePickerLoader());
    }

    @JSMethod
    @SuppressWarnings("unused")
    public boolean pickImage(HashMap<String, Object> options, final JSCallback callback) {
        Context context = mWXSDKInstance.getContext();
        if (context == null || !(context instanceof Activity)) {
            return false;
        }
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true);
        activity.startActivityForResult(intent, 120);
        mCallback = callback;
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList images = (ArrayList) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
        mCallback.invoke(images);
    }

    @Override
    public void destroy() {
    }
}
