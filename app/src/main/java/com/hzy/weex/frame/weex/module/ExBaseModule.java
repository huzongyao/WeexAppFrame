package com.hzy.weex.frame.weex.module;

import com.taobao.weex.common.WXModule;

import java.util.Map;

public class ExBaseModule extends WXModule {

    @SuppressWarnings("unchecked")
    protected <T> T getOption(Map<String, Object> options, String key, T defValue) {
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
