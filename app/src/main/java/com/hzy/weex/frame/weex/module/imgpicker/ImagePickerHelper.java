package com.hzy.weex.frame.weex.module.imgpicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public enum ImagePickerHelper {

    INSTANCE;

    private SimpleDateFormat FILE_NAME_FORMAT;
    private Random mRandom;

    ImagePickerHelper() {
        FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        mRandom = new Random();
    }

    private String getFileNameDate() {
        return FILE_NAME_FORMAT.format(new Date());
    }

    /**
     * use external path first
     */
    public File newPublicImageFile() {
        String filePath = PathUtils.getExternalPicturesPath();
        if (StringUtils.isTrimEmpty(filePath)) {
            filePath = PathUtils.getExternalAppPicturesPath();
        }
        if (StringUtils.isTrimEmpty(filePath)) {
            filePath = PathUtils.getInternalAppFilesPath();
        }
        String fileName = "wx_" + getFileNameDate() + mRandom.nextInt(999) + ".jpg";
        return new File(filePath, fileName);
    }

    /**
     * use app private space only
     * permissions is not required
     */
    public String newPrivateImagePath() {
        String filePath = PathUtils.getExternalAppPicturesPath();
        if (StringUtils.isTrimEmpty(filePath)) {
            filePath = PathUtils.getExternalAppDcimPath();
        }
        if (StringUtils.isTrimEmpty(filePath)) {
            filePath = PathUtils.getInternalAppFilesPath();
        }
        String fileName = "wx_" + getFileNameDate() + mRandom.nextInt(999) + ".jpg";
        return new File(filePath, fileName).getAbsolutePath();
    }

    public boolean compressImage(String srcPath, String outPath, int maxW, int maxH, int quality) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcPath, options);
            int width = options.outWidth;
            int height = options.outHeight;
            int widthScale = 1;
            int heightScale = 1;
            if (width > maxW) {
                widthScale = width / maxW;
            }
            if (height > maxH) {
                heightScale = height / maxH;
            }
            options.inSampleSize = Math.max(widthScale, heightScale);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
            FileOutputStream fos = new FileOutputStream(outPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.close();
            bitmap.recycle();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
