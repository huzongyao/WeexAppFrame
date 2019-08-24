package com.hzy.weex.frame.activity.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;

@SuppressLint("Registered")
public class SimpleWeexActivity extends AbstractWeexActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainer(findViewById(android.R.id.content));
    }
}
