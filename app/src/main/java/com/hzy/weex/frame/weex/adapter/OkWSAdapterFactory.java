package com.hzy.weex.frame.weex.adapter;

import com.taobao.weex.appfram.websocket.IWebSocketAdapter;
import com.taobao.weex.appfram.websocket.IWebSocketAdapterFactory;

public class OkWSAdapterFactory implements IWebSocketAdapterFactory {

    @Override
    public IWebSocketAdapter createWebSocketAdapter() {
        return new OkWebSocketAdapter();
    }
}
