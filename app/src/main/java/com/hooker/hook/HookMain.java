package com.hooker.hook;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.hooker.utils.AlipayTools;
import com.hooker.utils.LogUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {

    private String targetPackageName = "com.eg.android.AlipayGphone";
    private String targetProcessName = targetPackageName;
    private boolean ishook = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpp) throws Throwable {
//        log("package = " + lpp.packageName + " process = " + lpp.processName);

        if (lpp.appInfo == null || (lpp.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }

        if (lpp.packageName.equals(targetPackageName)) {
            if (targetProcessName.equals(lpp.processName)) {
                log("start hook " + targetProcessName);
                hooker(lpp);
            }
        }

    }

    private void hooker(final XC_LoadPackage.LoadPackageParam lpp) {
        try {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (ishook) return;

                    if (lpp.processName.equals(targetPackageName)) {
                        log("Application attach");
                        ishook = true;

                        Context context = (Context) param.args[0];
                        ClassLoader classLoader = context.getClassLoader();

                        HookAlipay.hooker(classLoader, context);
                        HookApp.hooker(classLoader, context);

                        log("app 启动...");

                        activityListen((Application) param.thisObject, classLoader);
                    }

                }
            });
        } catch (Exception e) {

        }
    }

    private void activityListen(Application application, final ClassLoader classLoader) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(final Activity activity) {
                String name = activity.getClass().getName();
                log("scene onResume - " + name);
                if (TextUtils.equals(name, "com.eg.android.AlipayGphone.AlipayLogin")) {
                    //账单详情
                    log("start get listDetail");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String listDetail = AlipayTools.getBillInfo(classLoader);
                            log("getBillListDetail = " + listDetail);
                        }
                    }).start();
                    //账单列表
                    log("start get list");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String list = AlipayTools.getBillList(classLoader);
                            log("bill list = " + list);
                        }
                    }).start();
                    //qrCode
                    log("start get qrCode");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String qrCode = AlipayTools.getQRCode(classLoader);
                            log("qrCode = " + qrCode);
                        }
                    }).start();
                    //hook RPC动态代理入口
                    log("start hook InvocationHandler");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AlipayTools.getRpcRunnable(classLoader);
                        }
                    }).start();
                    //打印三天内的账单详情
                    log("start filter list");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String filterStr = AlipayTools.getFilterList(classLoader);
                            log("filter list："+filterStr);
                        }
                    }).start();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                log("scene onPause - " + activity.getClass().getName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private static void log(String msg) {
        Log.d("tag", "hooker - " + msg);
    }
}
