package com.hzy.weex.frame.weex.module.imgpicker;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

public class GlideImagePickerLoader implements ImageLoader {

    public GlideImagePickerLoader() {
    }

    private void loadWithSize(Activity activity, String path, ImageView imageView, int width, int height) {
        RequestOptions opts = PickerRequestOptions.INSTANCE.requestOptions.override(width, height);
        Glide.with(activity).load(Uri.fromFile(new File(path))).apply(opts).into(imageView);
    }

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        loadWithSize(activity, path, imageView, width, height);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        loadWithSize(activity, path, imageView, width, height);
    }

    @Override
    public void clearMemoryCache() {
    }
}
