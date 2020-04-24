package com.hzy.weex.frame.weex.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

public class FrescoImageAdapter implements IWXImgLoaderAdapter {

    private BaseControllerListener<ImageInfo> mControllerListener;

    public FrescoImageAdapter() {
        mControllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                LogUtils.e("Image Load Failed: " + id);
            }
        };
    }

    @Override
    public void setImage(String url, ImageView view, WXImageQuality quality, WXImageStrategy strategy) {
        if (view == null || view.getLayoutParams() == null) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            view.setImageBitmap(null);
            return;
        }
        String temp = url;
        if (url.startsWith("//")) {
            temp = "http:" + url;
        }
        Uri uri = Uri.parse(temp);
        int viewWith = view.getLayoutParams().width;
        int viewHeight = view.getLayoutParams().height;
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setImageDecodeOptions(ImageDecodeOptions.newBuilder()
                        .setMaxDimensionPx(Math.max(viewWith, viewHeight)).build())
                .build();
        if (view instanceof DraweeView) {
            DraweeView draweeView = (DraweeView) view;
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setImageRequest(request)
                    .setControllerListener(mControllerListener)
                    .build();
            WXSDKManager.getInstance().postOnUiThread(
                    () -> draweeView.setController(controller), 0);
        } else {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>> dataSource
                    = imagePipeline.fetchDecodedImage(request, null);
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    view.setImageBitmap(bitmap);
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                }
            }, UiThreadImmediateExecutorService.getInstance());
        }
    }
}
