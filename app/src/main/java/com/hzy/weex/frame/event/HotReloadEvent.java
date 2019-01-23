package com.hzy.weex.frame.event;

public class HotReloadEvent {

    //hot refresh
    public static final int HOT_REFRESH_CONNECT = 0x111;
    public static final int HOT_REFRESH_DISCONNECT = HOT_REFRESH_CONNECT + 1;
    public static final int HOT_REFRESH_REFRESH = HOT_REFRESH_DISCONNECT + 1;

    public int type;
    public String text;

    public HotReloadEvent(int type, String text) {
        this.type = type;
        this.text = text;
    }
}
