package com.hzy.weex.frame.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hzy.weex.frame.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.core.ScanBoxView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRScanActivity extends AppCompatActivity
        implements QRCodeView.Delegate {

    public static final String EXTRA_CONTENT = "EXTRA_CONTENT";
    private static final long SCAN_TIME_OUT = 30_000L;
    private ZXingView mZXingView;
    private String mAmbientBrightnessTip;
    private Timer mFinishTimer;
    private ImageView mFlashTogger;
    private boolean mIsFlashOn = false;
    private TimerTask mFishTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAmbientBrightnessTip = getString(R.string.qr_tips_too_dark);
        mZXingView = findViewById(R.id.zxing_view);
        mFlashTogger = findViewById(R.id.button_flash);
        mZXingView.setDelegate(this);
        mFishTask = new TimerTask() {
            @Override
            public void run() {
                ToastUtils.showShort(R.string.qr_tip_scan_timeout);
                finish();
            }
        };
        mFlashTogger.setOnClickListener(v -> switchFlashLight());
    }

    private void switchFlashLight() {
        if (mIsFlashOn) {
            mZXingView.closeFlashlight();
            mFlashTogger.setImageResource(R.drawable.ic_flash_off);
        } else {
            mZXingView.openFlashlight();
            mFlashTogger.setImageResource(R.drawable.ic_flash_on);
        }
        mIsFlashOn = !mIsFlashOn;
    }

    @Override
    protected void onStart() {
        super.onStart();
        startCameraAndSpot();
    }

    private void startCameraAndSpot() {
        PermissionUtils.permission(Manifest.permission.CAMERA)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        mIsFlashOn = false;
                        mFlashTogger.setVisibility(View.GONE);
                        mZXingView.startCamera();
                        mZXingView.startSpotAndShowRect();
                        mFinishTimer = new Timer();
                        mFinishTimer.schedule(mFishTask, SCAN_TIME_OUT);
                    }

                    @Override
                    public void onDenied() {
                    }
                }).request();
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera();
        mFinishTimer.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        Intent intent = new Intent(this, WXPageActivity.class);
        intent.setData(Uri.parse(result));
        startActivity(intent);
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = mZXingView.getScanBoxView().getTipText();
        ScanBoxView scanBoxView = mZXingView.getScanBoxView();
        int toggleVisible = View.VISIBLE;
        if (isDark) {
            if (!tipText.contains(mAmbientBrightnessTip)) {
                scanBoxView.setTipText(tipText + mAmbientBrightnessTip);
            }
        } else {
            if (tipText.contains(mAmbientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(mAmbientBrightnessTip));
                scanBoxView.setTipText(tipText);
            }
            if (!mIsFlashOn) {
                toggleVisible = View.GONE;
            }
        }
        if (mFlashTogger.getVisibility() != toggleVisible) {
            mFlashTogger.setVisibility(toggleVisible);
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }
}
