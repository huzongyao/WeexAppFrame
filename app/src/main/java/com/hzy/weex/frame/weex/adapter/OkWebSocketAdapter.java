package com.hzy.weex.frame.weex.adapter;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.taobao.weex.appfram.websocket.IWebSocketAdapter;
import com.taobao.weex.appfram.websocket.WebSocketCloseCodes;

import java.io.EOFException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class OkWebSocketAdapter implements IWebSocketAdapter {

    private final OkHttpClient mOkHttpClient;
    private WebSocket mWebSocket;
    private EventListener mEventListener;

    public OkWebSocketAdapter() {
        mOkHttpClient = new OkHttpClient();
    }

    @Override
    public void connect(String url, @Nullable final String protocol, EventListener listener) {
        this.mEventListener = listener;

        Request.Builder builder = new Request.Builder();
        if (protocol != null) {
            builder.addHeader(HEADER_SEC_WEBSOCKET_PROTOCOL, protocol);
        }
        builder.url(url);
        Request wsRequest = builder.build();
        mOkHttpClient.newWebSocket(wsRequest, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                OkWebSocketAdapter.this.mWebSocket = webSocket;
                mEventListener.onOpen();
                LogUtils.e("onOpen: " + response.message());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                mEventListener.onMessage(text);
                LogUtils.e("onMessage: " + text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                mEventListener.onClose(code, reason, true);
                LogUtils.e("onClosed: " + code);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable e, Response response) {
                e.printStackTrace();
                if (e instanceof EOFException) {
                    mEventListener.onClose(WebSocketCloseCodes.CLOSE_NORMAL.getCode(),
                            WebSocketCloseCodes.CLOSE_NORMAL.name(), true);
                } else {
                    mEventListener.onError(e.getMessage());
                }
            }
        });
    }

    @Override
    public void send(String data) {
        if (mWebSocket != null) {
            try {
                mWebSocket.send(data);
                LogUtils.e("send: " + data);
            } catch (Exception e) {
                e.printStackTrace();
                reportError(e.getMessage());
            }
        } else {
            reportError("WebSocket is not ready");
        }
    }

    @Override
    public void close(int code, String reason) {
        if (mWebSocket != null) {
            try {
                mWebSocket.close(code, reason);
                LogUtils.e("close: " + code);
            } catch (Exception e) {
                e.printStackTrace();
                reportError(e.getMessage());
            }
        }
    }

    @Override
    public void destroy() {
        if (mWebSocket != null) {
            try {
                mWebSocket.close(WebSocketCloseCodes.CLOSE_GOING_AWAY.getCode(),
                        WebSocketCloseCodes.CLOSE_GOING_AWAY.name());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reportError(String message) {
        if (mEventListener != null) {
            mEventListener.onError(message);
        }
    }
}
