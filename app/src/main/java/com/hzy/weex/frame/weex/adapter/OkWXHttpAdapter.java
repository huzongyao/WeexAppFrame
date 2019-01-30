package com.hzy.weex.frame.weex.adapter;

import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.taobao.weex.adapter.IWXHttpAdapter;
import com.taobao.weex.common.WXRequest;
import com.taobao.weex.common.WXResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

public class OkWXHttpAdapter implements IWXHttpAdapter {

    public static final long DEFAULT_CACHE_SIZE = 80_000_000L;
    private final OkHttpClient mOkHttpClient;

    public OkWXHttpAdapter() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cache(new Cache(Utils.getApp().getCacheDir(), DEFAULT_CACHE_SIZE))
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    public void sendRequest(WXRequest request, OnHttpListener listener) {
        String method = request.method == null ? "GET" : request.method.toUpperCase();
        String bodyString = request.body == null ? "{}" : request.body;

        RequestBody body = HttpMethod.requiresRequestBody(method) ?
                RequestBody.create(null, bodyString) : null;

        Headers.Builder headersBuilder = new Headers.Builder();
        if (request.paramMap != null) {
            for (Map.Entry<String, String> param : request.paramMap.entrySet()) {
                headersBuilder.add(param.getKey(), param.getValue());
            }
        }

        if (TextUtils.isEmpty(headersBuilder.get("User-Agent"))) {
            headersBuilder.add("User-Agent", "Mozilla/5.0 (Linux)");
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(request.url).headers(headersBuilder.build())
                .method(method, body);

        mOkHttpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                WXResponse wxResponse = new WXResponse();
                wxResponse.errorMsg = e.getMessage();
                wxResponse.errorCode = "-1";
                wxResponse.statusCode = "-1";
                listener.onHttpFinish(wxResponse);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                WXResponse wxResponse = new WXResponse();
                byte[] responseBody = new byte[0];
                try {
                    responseBody = response.body().bytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                wxResponse.data = new String(responseBody);
                wxResponse.statusCode = String.valueOf(response.code());
                wxResponse.originalData = responseBody;
                wxResponse.extendParams = new HashMap<>();

                Headers headers = response.headers();
                for (String name : headers.names()) {
                    wxResponse.extendParams.put(name, headers.get(name));
                }
                if (response.code() < 200 || response.code() > 299) {
                    wxResponse.errorMsg = response.message();
                }
                listener.onHttpFinish(wxResponse);
            }
        });
    }
}
