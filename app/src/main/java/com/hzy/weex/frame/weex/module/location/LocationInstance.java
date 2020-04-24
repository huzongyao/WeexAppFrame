package com.hzy.weex.frame.weex.module.location;

import android.annotation.SuppressLint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.taobao.weex.bridge.JSCallback;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;


public enum LocationInstance implements TencentLocationListener {
    INSTANCE;

    private TencentLocationManager mLocationManager;
    private TencentLocationRequest mLocationRequest;
    private JSCallback mJsCallback;

    LocationInstance() {
        try {
            mLocationRequest = TencentLocationRequest.create()
                    .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI)
                    .setAllowGPS(true);
            mLocationManager = TencentLocationManager.getInstance(Utils.getApp());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation(JSCallback callback) {
        try {
            mJsCallback = callback;
            start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        try {
            if (mJsCallback != null) {
                if (TencentLocation.ERROR_OK == error) {
                    mJsCallback.invoke(saveLocation(location));
                } else {
                    mJsCallback.invoke(loadLocation());
                }
            }
            stop();
            LogUtils.d("onLocationChanged la:" + location.getLatitude() +
                    " lo:" + location.getLongitude() + " addr:" + location.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        LogUtils.d("onStatusUpdate name:" + name + " status:" + status + " desc:" + desc);
    }

    /**
     * 开始定位
     */
    private void start() {
        try {
            int error = mLocationManager.requestLocationUpdates(mLocationRequest, this);
            if (error != 0) {
                stop();
                if (mJsCallback != null) {
                    mJsCallback.invoke(loadLocation());
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止定位
     */
    private void stop() {
        try {
            mLocationManager.removeUpdates(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject saveLocation(TencentLocation location) {
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("latitude", String.valueOf(location.getLatitude()));
            jObj.put("longitude", String.valueOf(location.getLongitude()));
            jObj.put("nation", location.getNation());
            jObj.put("province", location.getProvince());
            jObj.put("city", location.getCity());
            jObj.put("district", location.getDistrict());
            jObj.put("town", location.getTown());
            jObj.put("village", location.getVillage());
            jObj.put("street", location.getStreet());
            String jsonString = jObj.toJSONString();
            SPUtils.getInstance().put("location", jsonString);
            LogUtils.d("Save Location: " + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jObj;
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
}
