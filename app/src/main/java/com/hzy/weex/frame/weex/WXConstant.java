package com.hzy.weex.frame.weex;

import com.hzy.weex.frame.BuildConfig;

public class WXConstant {
    public static final String WEEX_TPL_KEY = "_wx_tpl";
    public static final String WX_PAGE_SCHEMA = "wxpage";
    public static final String WX_SHOW_ACTION_BAR = "actionbar";

    // debug env show toolbar
    public static final String DEFAULT_WX_URL = BuildConfig.DEBUG ?
            "http://huzongyao.gitee.io/weex-lite-app/assets/splash-page.js?actionbar=true" :
            "http://huzongyao.gitee.io/weex-lite-app/assets/splash-page.js";
}
