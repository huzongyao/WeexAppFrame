package com.hzy.weex.frame.weex.https;

import android.text.TextUtils;

import com.hzy.weex.frame.event.HotReloadEvent;

import org.greenrobot.eventbus.EventBus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public enum HotRefreshManager {

    INSTANCE;

    private WebSocket mWebSocket = null;

    public boolean disConnect() {
        if (mWebSocket != null) {
            try {
                mWebSocket.close(1000, "activity finish!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean connect(String url) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url)
                .addHeader("sec-websocket-protocol", "echo-protocol").build();
        httpClient.newWebSocket(request, new WXWebSocketListener(url));
        return true;
    }

    class WXWebSocketListener extends WebSocketListener {
        private String mUrl;

        WXWebSocketListener(String url) {
            mUrl = url;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            mWebSocket = webSocket;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            if (TextUtils.equals("refresh", text)) {
                EventBus.getDefault()
                        .post(new HotReloadEvent(HotReloadEvent.HOT_REFRESH_REFRESH, mUrl));
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            mWebSocket = null;
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            mWebSocket = null;
        }
    }
}
