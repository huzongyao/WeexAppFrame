package com.hzy.weex.frame.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.hzy.weex.frame.R;

public class StatusLayout extends FrameLayout {

    private final View mPageLayout;
    private final ProgressBar mProgressBar;
    private final View mInfoLayout;

    public StatusLayout(@NonNull Context context) {
        this(context, null);
    }

    public StatusLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPageLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_status, this);
        mProgressBar = mPageLayout.findViewById(R.id.progress_bar);
        mInfoLayout = mPageLayout.findViewById(R.id.info_layout);
    }

    public void setOnInfoClickListener(OnClickListener listener) {
        mInfoLayout.setOnClickListener(listener);
    }

    public void hide() {
        mPageLayout.setVisibility(GONE);
    }

    public void loading() {
        mPageLayout.setVisibility(VISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mInfoLayout.setVisibility(GONE);
    }

    public void error() {
        mPageLayout.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mInfoLayout.setVisibility(VISIBLE);
    }
}
