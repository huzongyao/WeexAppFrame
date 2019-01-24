package com.hzy.weex.frame.weex.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

public class GlideImageAdapter implements IWXImgLoaderAdapter {

    private final RequestOptions mRequestOptions;

    public GlideImageAdapter() {
        mRequestOptions = new RequestOptions();
    }

    @Override
    public void setImage(final String url, final ImageView view, WXImageQuality quality,
                         final WXImageStrategy strategy) {
        Runnable runnable = () -> {
            try {
                if (view == null || view.getLayoutParams() == null) {
                    return;
                }
                String placeHolder = "";
                if (strategy != null) {
                    placeHolder = strategy.placeHolder;
                }
                boolean placeHolderEmpty = StringUtils.isTrimEmpty(placeHolder);
                boolean imageEmpty = StringUtils.isTrimEmpty(url);
                if (placeHolderEmpty && imageEmpty) {
                    view.setImageBitmap(null);
                    return;
                }
                Context context = Utils.getApp();
                if (!placeHolderEmpty) {
                    Glide.with(context).load(Uri.parse(placeHolder))
                            .apply(mRequestOptions).into(view);
                }
                if (!imageEmpty) {
                    String imageUrl = url;
                    if (url.startsWith("//")) {
                        imageUrl = "http:" + url;
                    }
                    Glide.with(context).load(imageUrl).apply(mRequestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade()).into(view);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            WXSDKManager.getInstance().postOnUiThread(runnable, 0);
        }
    }
}
