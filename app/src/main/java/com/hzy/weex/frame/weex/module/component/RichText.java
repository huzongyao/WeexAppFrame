package com.hzy.weex.frame.weex.module.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

public class RichText extends WXComponent<TextView> {

    public RichText(WXSDKInstance instance, WXVContainer parent,
                    BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    @Override
    protected TextView initComponentHostView(@NonNull Context context) {
        TextView view = new TextView(context);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @SuppressWarnings("unused")
    @WXComponentProp(name = "tel")
    public void setTelLink(String tel) {
        SpannableString spannable = new SpannableString(tel);
        spannable.setSpan(new URLSpan("tel:" + tel), 0, tel.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getHostView().setText(spannable);
    }
}
