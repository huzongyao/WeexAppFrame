package com.hzy.weex.frame.event;

public class HttpResultEvent {

    public static final int HTTP_RESULT_OK = 0x111;
    public static final int HTTP_RESULT_FAIL = HTTP_RESULT_OK + 1;

    public int status;
    public String url;
    public String text;
    public String instanceId;

    public HttpResultEvent(String instanceId, int status, String url) {
        this.instanceId = instanceId;
        this.status = status;
        this.url = url;
    }

    public HttpResultEvent(String instanceId, int status, String url, String text) {
        this.instanceId = instanceId;
        this.status = status;
        this.url = url;
        this.text = text;
    }
}
