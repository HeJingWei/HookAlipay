package com.hooker.hook;

import android.app.Activity;
import android.content.Context;

import com.hooker.utils.HookHelper;
import com.hooker.utils.LogUtils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HookAlipay {

    private static void log(String msg) {
        LogUtils.log(msg);
    }

    public static void hooker(ClassLoader classLoader, Context context) throws Throwable {
        hook(classLoader, context);
    }

    private static void hook(final ClassLoader classLoader, final Context context) throws Throwable {

        log("CIHooker");
        CIHooker(classLoader);
        log("hookNebula");
        log("hookScanAttack");
        hookScanAttack(classLoader);

        log("hookNXResourceUtils");
        log("hookEnvironmentInfo");
        hookEnvironmentInfo(classLoader, context);
        hookEnvironmentDetector(classLoader, context);

        log("hookDeviceInfo");
        hookDeviceInfo(classLoader, context);
        log("hookUtils");
        hookUtils(classLoader, context);
        log("hookAlipayWalletUtil");
        hookAlipayWalletUtil(classLoader, context);
        log("hookMonitorUtils");
        hookMonitorUtils(classLoader, context);
        log("hookNoName1");
        hookNoName1(classLoader);
        hookNoName2(classLoader);

        hookRpcTestService(classLoader, context);

        log("alipay hook end");

    }

    private static void CIHooker(ClassLoader classLoader) throws Throwable {
        Class<?> CI = HookHelper.getClass("com.alipay.mobile.base.security.CI", classLoader);
        if (CI == null) {
            return;
        }

        XposedHelpers.findAndHookMethod(CI, "a", String.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object object = param.getResult();
                XposedHelpers.setBooleanField(object, "a", false);
                XposedHelpers.setObjectField(object, "b", "");
                param.setResult(object);
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod(CI, "a", Class.class, String.class, String.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return (byte) 0;
            }
        });
        XposedHelpers.findAndHookMethod(CI, "a", ClassLoader.class, String.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                return (byte) 0;
            }
        });
        XposedHelpers.findAndHookMethod(CI, "a", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return false;
            }
        });

        XposedHelpers.findAndHookMethod(CI, "a", CI, Activity.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                return null;
            }
        });

    }

    private static void hookScanAttack(ClassLoader classLoader) throws Throwable {
        Class<?> ScanAttack = HookHelper.getClass("com.alipay.apmobilesecuritysdk.scanattack.common.ScanAttack", classLoader);
        if (ScanAttack == null) {
            return;
        }

        log("hook ScanAttack");

        XposedHelpers.findAndHookMethod(ScanAttack, "xpExceptionCatch", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return false;
            }
        });

        XposedHelpers.findAndHookMethod(ScanAttack, "xpFieldInHook", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return false;
            }
        });

        XposedHelpers.findAndHookMethod(ScanAttack, "xpInstalled", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return false;
            }
        });

        XposedHelpers.findAndHookMethod(ScanAttack, "xpMethodInHook", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return null;
            }
        });

        XposedHelpers.findAndHookMethod(ScanAttack, "cyExceptionCatch", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return false;
            }
        });

        XposedBridge.hookAllMethods(ScanAttack, "getScanAttackInfo", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return new String[]{};
            }
        });

        log("hook ScanAttack end");
    }

    private static void hookEnvironmentInfo(final ClassLoader classLoader, final Context context) throws Throwable {
        final Class<?> EnvironmentInfo = HookHelper.getClass("com.alipay.security.mobile.module.deviceinfo.EnvironmentInfo", classLoader);
        if (EnvironmentInfo == null) {
            log("EnvironmentInfo == null");
            return;
        }

        // 检测root
        XposedHelpers.findAndHookMethod(EnvironmentInfo, "c", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

        // 检测虚拟机
        XposedHelpers.findAndHookMethod(EnvironmentInfo, "a", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });


    }

    private static void hookEnvironmentDetector(final ClassLoader classLoader, final Context context) throws Throwable {
        final Class<?> EnvironmentDetector = HookHelper.getClass("com.taobao.securityjni.EnvironmentDetector", classLoader);
        if (EnvironmentDetector == null) {
            log("EnvironmentDetector == null");
            return;
        }

        log("hook EnvironmentDetector");

        XposedHelpers.findAndHookMethod(EnvironmentDetector, "isRoot", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

        log("hook isSimulator");
        XposedHelpers.findAndHookMethod(EnvironmentDetector, "isSimulator", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });


    }

    private static void hookDeviceInfo(final ClassLoader classLoader, final Context context) throws Throwable {
        final Class<?> DeviceInfo = HookHelper.getClass("com.alipay.android.msp.framework.sys.DeviceInfo", classLoader);
        if (DeviceInfo == null) {
            log("DeviceInfo == null");
            return;
        }

        // 检测 root
        XposedHelpers.findAndHookMethod(DeviceInfo, "j", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return "0";
            }
        });

    }

    private static void hookUtils(final ClassLoader classLoader, final Context context) throws Throwable {
        final Class<?> Utils = HookHelper.getClass("com.alipay.android.msp.utils.Utils", classLoader);
        if (Utils == null) {
            log("Utils == null");
            return;
        }

        XposedHelpers.findAndHookMethod(Utils, "isDeviceRooted", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

        XposedHelpers.findAndHookMethod(Utils, "findBinary", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

    }

    private static void hookAlipayWalletUtil(final ClassLoader classLoader, final Context context) throws Throwable {
        final Class<?> Utils = HookHelper.getClass("com.alipay.security.mobile.util.AlipayWalletUtil", classLoader);
        if (Utils == null) {
            log("Utils == null");
            return;
        }

        XposedHelpers.findAndHookMethod(Utils, "isDeviceRooted", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return 0;
            }
        });

    }

    private static void hookNoName1(final ClassLoader classLoader) throws Throwable {
        final Class<?> Cls = HookHelper.getClass("com.alipay.berserker.f.i", classLoader);
        if (Cls == null) {
            log("com.alipay.berserker.f.i == null");
            return;
        }

        // 检测su root
        XposedHelpers.findAndHookMethod(Cls, "a", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

    }

    private static void hookNoName2(final ClassLoader classLoader) throws Throwable {
        final Class<?> Cls = HookHelper.getClass("com.ta.audid.a.f", classLoader);
        if (Cls == null) {
            log("Cls == null");
            return;
        }

        XposedHelpers.findAndHookMethod(Cls, "isEmulator", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

    }

    private static void hookMonitorUtils(final ClassLoader classLoader, final Context context) throws Throwable {
        final Class<?> MonitorUtils = HookHelper.getClass("com.alipay.mobile.monitor.util.MonitorUtils", classLoader);
        if (MonitorUtils == null) {
            log("MonitorUtils == null");
            return;
        }

        XposedHelpers.findAndHookMethod(MonitorUtils, "isDeviceRooted", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

        XposedHelpers.findAndHookMethod(MonitorUtils, "isDeviceEmulator", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });

    }

    private static void hookRpcTestService(final ClassLoader classLoader, final Context context) throws Throwable {
        final Class<?> Cls = HookHelper.getClass("com.alipay.mobile.common.transportext.biz.rpctest.RpcTestService", classLoader);
        if (Cls == null) {
            log("RpcTestService == null");
            return;
        }

        XposedHelpers.findAndHookMethod(Cls, "sendTestRpc", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return null;
            }
        });


    }

}
