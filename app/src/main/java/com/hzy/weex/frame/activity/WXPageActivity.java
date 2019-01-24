package com.hzy.weex.frame.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hzy.weex.frame.BuildConfig;
import com.hzy.weex.frame.R;
import com.hzy.weex.frame.activity.base.WXBaseActivity;
import com.hzy.weex.frame.constant.RequestCode;
import com.hzy.weex.frame.constant.RouterHub;
import com.hzy.weex.frame.event.HotReloadEvent;
import com.hzy.weex.frame.event.HttpResultEvent;
import com.hzy.weex.frame.weex.WXConstant;
import com.hzy.weex.frame.weex.adapter.NavigatorAdapter;
import com.hzy.weex.frame.weex.adapter.WXAnalyzerDelegate;
import com.hzy.weex.frame.weex.https.HotRefreshManager;
import com.hzy.weex.frame.weex.https.WXScriptHttpLoader;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.RenderContainer;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.ui.component.NestedContainer;
import com.taobao.weex.utils.WXFileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

@Route(path = RouterHub.WX_PAGE_ACTIVITY)
public class WXPageActivity extends WXBaseActivity
        implements IWXRenderListener, WXSDKInstance.NestedInstanceInterceptor {

    private static final String TAG = "WXPageActivity";

    private Toolbar mToolbar;
    private FrameLayout mContentView;
    private ProgressBar mProgressBar;

    private boolean mHasStatusBar = true;
    private boolean mHasToolBar = true;
    private BroadcastReceiver mReceiver;
    private Uri mUri;
    private WXSDKInstance mInstance;
    private HashMap<String, Object> mConfigMap = new HashMap<>();
    private WXAnalyzerDelegate mWxAnalyzerDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weex);
        WXSDKEngine.setActivityNavBarSetter(new NavigatorAdapter());
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        loadUriFromIntent();
        initUIViews();
        loadPageFromUri();
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wx_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                reloadWXPage();
                break;
            case R.id.menu_qr_scan:
                startScanQrCode();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startScanQrCode() {
        Intent intent = new Intent(this, QRScanActivity.class);
        startActivityForResult(intent, RequestCode.REQUEST_CODE_SCAN_QR_CODE);
    }

    private void reloadWXPage() {
        String scheme = mUri.getScheme();
        if (mUri.isHierarchical() && (TextUtils.equals(scheme, "http")
                || TextUtils.equals(scheme, "https"))) {
            String weexTpl = mUri.getQueryParameter(WXConstant.WEEX_TPL_KEY);
            String url = TextUtils.isEmpty(weexTpl) ? mUri.toString() : weexTpl;
            loadWXFromService(url);
        }
    }

    private void loadPageFromUri() {
        if (WXConstant.WX_PAGE_SCHEMA.equals(mUri.getScheme()) ||
                TextUtils.equals("true", mUri.getQueryParameter("_wxpage"))) {
            mUri = mUri.buildUpon().scheme("http").build();
            loadWXFromService(mUri.toString());
            startHotRefresh();
        } else if (TextUtils.equals("http", mUri.getScheme()) ||
                TextUtils.equals("https", mUri.getScheme())) {
            String weexTpl = mUri.getQueryParameter(WXConstant.WEEX_TPL_KEY);
            String url = TextUtils.isEmpty(weexTpl) ? mUri.toString() : weexTpl;
            loadWXFromService(url);
            startHotRefresh();
        } else {
            loadWXFromLocal(false);
        }
        mInstance.onActivityCreate();
        registerBroadcastReceiver();
        mWxAnalyzerDelegate = new WXAnalyzerDelegate(this);
        mWxAnalyzerDelegate.onCreate();
    }

    private void loadWXFromService(final String url) {
        mProgressBar.setVisibility(View.VISIBLE);
        if (mInstance != null) {
            mInstance.destroy();
        }
        RenderContainer renderContainer = new RenderContainer(this);
        mInstance = new WXSDKInstance(this);
        mInstance.setRenderContainer(renderContainer);
        mInstance.registerRenderListener(this);
        mInstance.setNestedInstanceInterceptor(this);
        mInstance.setTrackComponent(true);
        mContentView.addView(renderContainer);
        WXScriptHttpLoader.INSTANCE.sendRequest(url);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onLoadService(HttpResultEvent event) {
        String url = event.url;
        switch (event.status) {
            case HttpResultEvent.HTTP_RESULT_OK:
                try {
                    mConfigMap.put("bundleUrl", url);
                    mInstance.render(TAG, event.text, mConfigMap,
                            null, WXRenderStrategy.APPEND_ASYNC);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case HttpResultEvent.HTTP_RESULT_FAIL:
                mProgressBar.setVisibility(View.GONE);
                ToastUtils.showShort("network error!");
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onNeedHotReload(HotReloadEvent event) {
        switch (event.type) {
            case HotReloadEvent.HOT_REFRESH_REFRESH:
                loadWXFromService(mUri.toString());
                break;
            default:
                break;
        }
    }

    private void loadWXFromLocal(boolean reload) {
        if (reload && mInstance != null) {
            mInstance.destroy();
            mInstance = null;
        }
        if (mInstance == null) {
            RenderContainer renderContainer = new RenderContainer(this);
            mInstance = new WXSDKInstance(this);
            mInstance.setRenderContainer(renderContainer);
            mInstance.registerRenderListener(this);
            mInstance.setNestedInstanceInterceptor(this);
            mInstance.setTrackComponent(true);
        }
        mContentView.post(() -> {
            Activity ctx = WXPageActivity.this;
            Rect outRect = new Rect();
            ctx.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
            mConfigMap.put(WXSDKInstance.BUNDLE_URL, mUri.toString());
            String path = "file".equals(mUri.getScheme()) ?
                    assembleFilePath(mUri) : mUri.toString();
            mInstance.render(path, WXFileUtils.loadAsset(path, ctx),
                    mConfigMap, null, WXRenderStrategy.APPEND_ASYNC);
        });
    }

    private String assembleFilePath(Uri uri) {
        if (uri != null && uri.getPath() != null) {
            return uri.getPath().replaceFirst("/", "");
        }
        return "";
    }

    /**
     * Hot reload for develop
     */
    private void startHotRefresh() {
        if (BuildConfig.DEBUG) {
            try {
                String host = new URL(mUri.toString()).getHost();
                String wsUrl = "ws://" + host + ":8082";
                HotRefreshManager.INSTANCE.connect(wsUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUriFromIntent() {
        Intent intent = getIntent();
        mUri = intent.getData();
        if (mUri != null) {
            mHasToolBar =
                    mUri.getBooleanQueryParameter(WXConstant.WX_HIDE_ACTION_BAR, true);
        }
        if (mUri == null) {
            mUri = Uri.parse(WXConstant.DEFAULT_WX_URL);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String bundleUrl = bundle.getString(WXSDKInstance.BUNDLE_URL);
            if (bundleUrl != null) {
                mConfigMap.put(WXSDKInstance.BUNDLE_URL, bundleUrl);
                mUri = Uri.parse(bundleUrl);
            }
        } else {
            mConfigMap.put(WXSDKInstance.BUNDLE_URL, mUri.toString());
        }
        if (mUri == null) {
            ToastUtils.showShort("URL error!");
            finish();
        }
    }

    private void initUIViews() {
        mToolbar = findViewById(R.id.weex_toolbar);
        setSupportActionBar(mToolbar);
        mContentView = findViewById(R.id.weex_content);
        mProgressBar = findViewById(R.id.weex_progress);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            String title = mUri.toString().substring(mUri.toString()
                    .lastIndexOf(File.separator) + 1);
            actionBar.setTitle(title);
        }
        if (!mHasStatusBar) {
            BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        }
        mToolbar.setVisibility(mHasToolBar ? View.VISIBLE : View.GONE);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }

    private void registerBroadcastReceiver() {
        mReceiver = new RefreshBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WXSDKInstance.ACTION_DEBUG_INSTANCE_REFRESH);
        filter.addAction(WXSDKInstance.ACTION_INSTANCE_RELOAD);
        registerReceiver(mReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        mReceiver = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mInstance != null) {
            mInstance.onActivityStart();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInstance != null) {
            mInstance.onActivityResume();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInstance != null) {
            mInstance.onActivityPause();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInstance != null) {
            mInstance.onActivityStop();
        }
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onStop();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mInstance.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        if (mInstance != null) {
            mInstance.onActivityDestroy();
        }
        mContentView = null;
        HotRefreshManager.INSTANCE.disConnect();
        unregisterBroadcastReceiver();
        EventBus.getDefault().unregister(this);
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return (mWxAnalyzerDelegate != null && mWxAnalyzerDelegate.onKeyUp(keyCode, event))
                || super.onKeyUp(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mInstance != null) {
            mInstance.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mInstance != null) {
            mInstance.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        View wrappedView = null;
        if (mWxAnalyzerDelegate != null) {
            wrappedView = mWxAnalyzerDelegate.onWeexViewCreated(instance, view);
        }
        if (wrappedView != null) {
            view = wrappedView;
        }
        if (view.getParent() == null) {
            mContentView.addView(view);
        }
        mContentView.requestLayout();
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onWeexRenderSuccess(instance);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        if (mWxAnalyzerDelegate != null) {
            mWxAnalyzerDelegate.onException(instance, errCode, msg);
        }
        mProgressBar.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(errCode) && errCode.contains("|")) {
            String[] errCodeList = errCode.split("\\|");
            String code = errCodeList[1];
            String codeType = errCode.substring(0, errCode.indexOf("|"));
            if (TextUtils.equals("1", codeType)) {
                String errMsg = "codeType:" + codeType + "\n" + " errCode:" + code + "\n" +
                        " ErrorInfo:" + msg;
                degradeAlert(errMsg);
            } else {
                Toast.makeText(getApplicationContext(), "errCode:" + errCode +
                        " Render ERROR:" + msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void degradeAlert(String errMsg) {
        new AlertDialog.Builder(this)
                .setTitle("Downgrade success")
                .setMessage(errMsg)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onCreateNestInstance(WXSDKInstance instance, NestedContainer container) {
        Log.d(TAG, "Nested Instance created.");
    }

    public class RefreshBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WXSDKInstance.ACTION_INSTANCE_RELOAD.equals(intent.getAction()) ||
                    WXSDKInstance.ACTION_DEBUG_INSTANCE_REFRESH.equals(intent.getAction())) {
                String myUrl = intent.getStringExtra("url");
                LogUtils.e("RefreshBroadcastReceiver reload onReceive: " + myUrl);
                if (mUri != null) {
                    if (TextUtils.equals(mUri.getScheme(), "http") ||
                            TextUtils.equals(mUri.getScheme(), "https")) {
                        String weexTpl = mUri.getQueryParameter(WXConstant.WEEX_TPL_KEY);
                        String url = TextUtils.isEmpty(weexTpl) ? mUri.toString() : weexTpl;
                        loadWXFromService(url);
                    } else {
                        loadWXFromLocal(true);
                    }
                }
            }
        }
    }
}
