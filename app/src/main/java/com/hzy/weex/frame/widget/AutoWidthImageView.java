package com.hzy.weex.frame.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class AutoWidthImageView extends android.support.v7.widget.AppCompatImageView {

    public AutoWidthImageView(Context context) {
        super(context);
    }

    public AutoWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoWidthImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int height = MeasureSpec.getSize(heightMeasureSpec);
            //高度根据使得图片的宽度充满屏幕计算而得
            int width = (int) Math.ceil((float) height * (float) d.getIntrinsicWidth()
                    / (float) d.getIntrinsicHeight());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
