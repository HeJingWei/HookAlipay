package com.hooker.hook;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HookApp {
    public static void hooker(ClassLoader classLoader, Context context) throws Throwable {
        hook(classLoader);
    }

    private static void hook(final ClassLoader classLoader) throws Throwable {


    }

    private static void log(String msg) {
        Log.d("tag", "hooker - " + msg);
    }
}
