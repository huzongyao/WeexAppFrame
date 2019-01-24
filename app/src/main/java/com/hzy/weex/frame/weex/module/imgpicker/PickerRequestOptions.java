package com.hzy.weex.frame.weex.module.imgpicker;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hzy.weex.frame.R;

public enum PickerRequestOptions {

    INSTANCE;

    public final RequestOptions requestOptions;

    PickerRequestOptions() {
        requestOptions = new RequestOptions()
                .error(R.drawable.ic_default_image)
                .placeholder(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }
}
