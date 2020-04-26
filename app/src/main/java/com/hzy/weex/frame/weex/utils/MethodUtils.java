package com.hzy.weex.frame.weex.utils;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;

import java.lang.reflect.Method;


public class MethodUtils {

    public static Object tryCallVoid(Object obj, String methodName) {
        Object ret = null;
        try {
            Class<?> clazz = obj.getClass();
            Method method = clazz.getMethod(methodName);
            ret = method.invoke(obj);
        } catch (Throwable ignored) {
        }
        return ret;
    }

    public static JSONObject callAllGet(Object obj, JSONObject jsonObject) {
        return callAllGet(obj, jsonObject, null);
    }

    public static JSONObject callAllGet(Object obj, JSONObject jsonObject, String[] subMethods) {
        try {
            Class clazz = obj.getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                try {
                    String methodName = method.getName();
                    if (methodName.startsWith("get")) {
                        Object ret = method.invoke(obj);
                        if (ret != null) {
                            String keyName = toLowerFirst(methodName.substring(3));
                            if (ret instanceof Number) {
                                jsonObject.put(keyName, ret);
                            } else if (ret instanceof CharSequence) {
                                CharSequence retString = (CharSequence) ret;
                                if (!StringUtils.isTrimEmpty(retString.toString())) {
                                    jsonObject.put(keyName, ret);
                                }
                            } else {
                                for (String meName : subMethods) {
                                    if (methodName.equals(meName)) {
                                        jsonObject.put(keyName, callAllGet(ret, new JSONObject()));
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (methodName.startsWith("is")) {
                        Object ret = method.invoke(obj);
                        if (ret != null) {
                            if (ret instanceof Boolean) {
                                jsonObject.put(methodName, ret);
                            }
                        }
                    } else if (methodName.startsWith("has")) {
                        Object ret = method.invoke(obj);
                        if (ret != null) {
                            if (ret instanceof Boolean) {
                                jsonObject.put(methodName, ret);
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
            return jsonObject;
        } catch (Throwable ignored) {
        }
        return jsonObject;
    }

    private static String toLowerFirst(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }
}
