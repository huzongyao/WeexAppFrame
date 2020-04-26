package com.hzy.weex.frame.weex.module.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hzy.weex.frame.weex.utils.MethodUtils;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.ui.module.WXDeviceInfoModule;

import java.util.List;


public class ExDeviceInfoModule extends WXDeviceInfoModule {

    @JSMethod(uiThread = false)
    @SuppressWarnings("unused")
    public void getWifiInfo(@Nullable JSCallback callback) {
        try {
            Context context = Utils.getApp();
            WifiManager wm = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo;
            if ((wm != null) && ((wifiInfo = wm.getConnectionInfo()) != null)) {
                JSONObject jObj = MethodUtils.callAllGet(wifiInfo, new JSONObject());
                if (callback != null) {
                    callback.invoke(jObj);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.invoke("");
        }
    }

    @JSMethod(uiThread = false)
    @SuppressWarnings("unused")
    public void getCellInfo(@Nullable JSCallback callback) {
        PermissionUtils.permission(PermissionConstants.LOCATION)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        JSONObject jsonObject = getAllCellInfo();
                        if (callback != null) {
                            callback.invoke(jsonObject);
                        }
                    }

                    @Override
                    public void onDenied() {
                        if (callback != null) {
                            callback.invoke("");
                        }
                    }
                }).request();
    }

    @JSMethod(uiThread = false)
    @SuppressWarnings("unused")
    public void getSensorsInfo(@Nullable JSCallback callback) {
        try {
            Context context = Utils.getApp();
            SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensors;
            if ((sm != null) && ((sensors = sm.getSensorList(Sensor.TYPE_ALL)) != null)) {
                JSONObject jsonObject = new JSONObject();
                if (sensors.size() > 0) {
                    JSONArray jsonArray = new JSONArray();
                    for (Sensor sensor : sensors) {
                        JSONObject jObj = MethodUtils.callAllGet(sensor, new JSONObject());
                        jsonArray.add(jObj);
                    }
                    jsonObject.put("sensors", jsonArray);
                }
                if (callback != null) {
                    callback.invoke(jsonObject);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.invoke("");
        }
    }

    @JSMethod(uiThread = false)
    @SuppressWarnings("unused")
    public void getInputMethodInfo(@Nullable JSCallback callback) {
        try {
            Context context = Utils.getApp();
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            List<InputMethodInfo> imms;
            if ((imm != null) && ((imms = imm.getInputMethodList()) != null)) {
                JSONObject jsonObject = new JSONObject();
                if (imms.size() > 0) {
                    JSONArray jsonArray = new JSONArray();
                    for (InputMethodInfo imi : imms) {
                        JSONObject jObj = MethodUtils.callAllGet(imi, new JSONObject());
                        jsonArray.add(jObj);
                    }
                    jsonObject.put("inputMethods", jsonArray);
                }
                if (callback != null) {
                    callback.invoke(jsonObject);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.invoke("");
        }
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    private JSONObject getAllCellInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            Context context = Utils.getApp();
            TelephonyManager tm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                List<CellInfo> allCells;
                if ((allCells = tm.getAllCellInfo()) != null) {
                    JSONArray jsonArray = new JSONArray();
                    for (CellInfo cellInfo : allCells) {
                        JSONObject jObj = MethodUtils.callAllGet(cellInfo, new JSONObject(),
                                new String[]{"getCellIdentity", "getCellSignalStrength"});
                        jsonArray.add(jObj);
                    }
                    jsonObject.put("allCells", jsonArray);
                }
                CellLocation location;
                if ((location = tm.getCellLocation()) != null) {
                    JSONObject jObj = MethodUtils.callAllGet(location, new JSONObject());
                    jsonObject.put("cellLocation", jObj);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
