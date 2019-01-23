package com.hzy.weex.frame.weex.adapter;

import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

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
            if (view == null || view.getLayoutParams() == null) {
                return;
            }
            if (null != strategy && !TextUtils.isEmpty(strategy.placeHolder)) {
                Glide.with(view).load(Uri.parse(strategy.placeHolder))
                        .apply(mRequestOptions).into(view);
            }
            if (TextUtils.isEmpty(url)) {
                view.setImageBitmap(null);
                return;
            }
            String temp = getFullUrl(url);
            Glide.with(view).load(temp).apply(mRequestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(view);
        };
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            WXSDKManager.getInstance().postOnUiThread(runnable, 0);
        }
    }

    private String getFullUrl(String url) {
        if (url.startsWith("//")) {
            return "http:" + url;
        }
        return url;
    }
}
