package com.hzy.weex.frame.weex.module.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.taobao.weex.bridge.JSCallback;

public enum LocationInstance {
    INSTANCE;

    private LocationManager mLocationManager;

    LocationInstance() {
    }

    public void getLocation(JSCallback callback) {
        Location location = getLastLocation();
        if (location != null) {
            callback.invoke(saveLocation(location));
        } else {
            start(callback);
        }
    }


    @SuppressLint("MissingPermission")
    public Location getLastLocation() {
        if (mLocationManager == null) {
            mLocationManager =
                    (LocationManager) Utils.getApp().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public void start(JSCallback callback) {
        if (mLocationManager == null) {
            mLocationManager =
                    (LocationManager) Utils.getApp().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000, 50, new WXLocationListener(callback));
        } catch (Throwable e) {
            e.printStackTrace();
        }
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

        private final JSCallback mCallback;

        WXLocationListener(JSCallback callback) {
            this.mCallback = callback;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mCallback.invoke(saveLocation(location));
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
