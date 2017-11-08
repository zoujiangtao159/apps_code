package com.appsflyer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shacharaharon on 26/02/2017.
 */

class Reflection {

    private static Method getMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes) {
        Method method = null;
        try {
            method = cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignored) {
        }
        assert method != null;
        method.setAccessible(true);
        return method;
    }

    public static Object invokeMethod(Class<?> cls, Object instance, String methodName, Class<?>[] parameterTypes, Object... args) {
        return invokeMethod(getMethod(cls, methodName, parameterTypes), instance, args);
    }

    private static Object invokeMethod(Method method, Object instance, Object... args) {
        Object result = null;
        try {
            result = method.invoke(instance, args);
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static Object invokeMethodThrowing(Class<?> cls, Object instance, String methodName, Class<?>[] parameterTypes, Object... args) {
        return invokeMethodThrowing(getMethod(cls, methodName, parameterTypes), instance, args);
    }

    private static Object invokeMethodThrowing(Method method, Object instance, Object... args) {
        Object result = null;
        try {
            result = method.invoke(instance, args);
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static Constructor<?> getConstructor(String className, Class<?>... paramTypes) {
        Constructor<?> constructor = null;
        try {
            Class<?> cls = getClass(className);
            assert cls != null;
            constructor = cls.getDeclaredConstructor(paramTypes);
            assert constructor != null;
            constructor.setAccessible(true);
        } catch (Throwable ignored) {
        }
        return constructor;
    }

    public static Class<?> getClass(String className) {
        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
        return cls;
    }

    public static String getPropertyByField(String property) {
        Class<?> afp = AppsFlyerProperties.class;
        String result;
        try {
            Field field = afp.getDeclaredField(property);
            field.setAccessible(true);
            result = (String) field.get(null);
        } catch (NoSuchFieldException e) {
            return "";
        } catch (IllegalAccessException e) {
            return "";
        }
        return result;
    }

    public static Field getField(Class cls, String fieldName) {
        Field field = null;
        try {
            field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
        }
        return field;
    }

    private static boolean hasClass(String classString) {
        return getClass(classString) != null;
    }

    static boolean isSafetyNetEnabled() {
        return hasClass("com.google.android.gms.safetynet.SafetyNet") &&
                hasClass("com.google.android.gms.safetynet.SafetyNetApi") &&
                hasClass("com.google.android.gms.common.api.GoogleApiClient") &&
                hasClass("com.google.android.gms.common.api.ResultCallback") &&
                hasClass("com.google.android.gms.common.api.Status");
    }
}
