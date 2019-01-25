package com.hzy.weex.frame.weex.module.imgpicker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UriUtils;
import com.hzy.weex.frame.R;
import com.hzy.weex.frame.constant.RequestCode;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 模块用于选择图片
 */
public class ImagePickerModule extends WXModule {

    private final ExecutorService mExecutor;
    private JSCallback mCallback;
    private Uri mPhotoUri;
    private String mSrcImgPath;
    private Map<String, Object> mImageOptions;

    public ImagePickerModule() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * pick a image from album
     *
     * @param options  options
     *                 maxSize：default 1000
     *                 quality：default 80
     * @param callback callback
     */
    @JSMethod
    @SuppressWarnings("unused")
    public boolean pickImage(Map<String, Object> options, final JSCallback callback) {
        Context context = mWXSDKInstance.getContext();
        if (context == null || !(context instanceof Activity)) {
            return false;
        }
        try {
            mImageOptions = options;
            Activity activity = (Activity) context;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent = Intent.createChooser(intent, activity.getString(R.string.choose_file_picker));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(intent, RequestCode.REQUEST_CODE_PICK_IMAGE);
                mCallback = callback;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * take a photo by camera
     *
     * @param options  options
     *                 maxSize：default 1000
     *                 quality：default 80
     * @param callback callback
     */
    @JSMethod
    @SuppressWarnings("unused")
    public boolean takePhoto(Map<String, Object> options, final JSCallback callback) {
        Context context = mWXSDKInstance.getContext();
        if (context == null || !(context instanceof Activity)) {
            return false;
        }
        try {
            mImageOptions = options;
            Activity activity = (Activity) context;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = ImagePickerHelper.INSTANCE.newPublicImageFile();
            mPhotoUri = UriUtils.file2Uri(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            intent = Intent.createChooser(intent, activity.getString(R.string.choose_photo_camera));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(intent, RequestCode.REQUEST_CODE_TAKE_PHOTO);
                mCallback = callback;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_PICK_IMAGE ||
                requestCode == RequestCode.REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == RequestCode.REQUEST_CODE_PICK_IMAGE) {
                    mPhotoUri = data.getData();
                }
                onImageResult();
            } else {
                if (mCallback != null) {
                    mCallback.invoke(null);
                }
            }
        }
    }

    private void onImageResult() {
        try {
            if (mCallback != null) {
                if (mPhotoUri == null) {
                    mCallback.invoke(null);
                } else {
                    // need to run on main thread
                    mSrcImgPath = UriUtils.uri2File(mPhotoUri).getAbsolutePath();
                    mExecutor.submit(this::compressImageAsync);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void compressImageAsync() {
        String outPath = ImagePickerHelper.INSTANCE.newPrivateImagePath();
        PermissionUtils.permission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        if (mImageOptions == null) {
                            mImageOptions = new HashMap<>();
                        }
                        int maxSize = getOption(mImageOptions, "maxSize", 1000);
                        int quality = getOption(mImageOptions, "quality", 80);
                        boolean result = ImagePickerHelper.INSTANCE.compressImage(mSrcImgPath,
                                outPath, maxSize, maxSize, quality);
                        if (result) {
                            mCallback.invoke(outPath);
                        } else {
                            mCallback.invoke(null);
                        }
                    }

                    @Override
                    public void onDenied() {
                    }
                }).request();
    }

    @SuppressWarnings("unchecked")
    private <T> T getOption(Map<String, Object> options, String key, T defValue) {
        Object value = options.get(key);
        if (value == null) {
            return defValue;
        } else {
            try {
                return (T) value;
            } catch (Exception e) {
                e.printStackTrace();
                return defValue;
            }
        }
    }
}
