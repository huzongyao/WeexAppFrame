package com.hzy.weex.frame.weex.module.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.taobao.weex.bridge.JSCallback;

public enum LocationInstance {
    INSTANCE;

    private LocationManager mLocationManager;

    LocationInstance() {
        mLocationManager = (LocationManager)
                Utils.getApp().getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void getLocation(JSCallback callback) {
        try {
            Location location = null;
            if (mLocationManager != null) {
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {
                callback.invoke(saveLocation(location));
            } else {
                callback.invoke(loadLocation());
            }
            start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void start() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000, 100, new WXLocationListener());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public JSONObject loadLocation() {
        String jsonString = SPUtils.getInstance().getString("location");
        try {
            JSONObject locationObject = JSON.parseObject(jsonString);
            if (locationObject != null) {
                return locationObject;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public JSONObject saveLocation(Location location) {
        if (location != null) {
            double l = location.getLatitude();
            double o = location.getLongitude();
            if (l != 0 && o != 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("latitude", String.valueOf(l));
                jsonObject.put("longitude", String.valueOf(o));
                SPUtils.getInstance().put("location", jsonObject.toJSONString());
                return jsonObject;
            }
        }
        return null;
    }

    private class WXLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mLocationManager.removeUpdates(this);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
