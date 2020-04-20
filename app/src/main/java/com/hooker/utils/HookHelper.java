package com.hooker.utils;

import de.robv.android.xposed.XposedHelpers;

public class HookHelper {

    public static Class<?> getClass(String classname, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(classname, classLoader);
        }
        catch (XposedHelpers.ClassNotFoundError e) {
            log("get class not found error = " + e);
            return null;
        }
        catch (Throwable e) {
            log("get class throw = " + e);
            return null;
        }
    }

    private static void log(String msg) {
        LogUtils.log(msg);
    }

}
