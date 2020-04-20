package com.hooker.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class LogUtils {
    private static final String i_default_flag = "- " + MLog.TAGNAME + " - hooker ";
    private static final String i_tcp_flag = "--" + MLog.TAGNAME + " tcp - ";
    private static final String i_default_tag = MLog.TAGNAME;

    public static void log(String msg) {
        log(i_default_tag, i_default_flag, msg);
    }

    public static void tcp(String msg) {
        log(i_default_tag, i_tcp_flag, msg);
    }

    public static void info(String msg) {
        log(i_default_tag, "- " + MLog.TAGNAME + " - info ", msg);
    }

    public static void dumpObjectFiled(String flag, Object object, Class<?> cls) {
        if (flag == null) flag = i_default_flag;
        MLog.dumpObjectFiled2(i_default_flag + flag, object, cls);
    }

    public static void dumpObjectAllFiled(String flag, Object object) {
        if (flag == null) {
            flag = i_default_flag;
        }
        else {
            flag = i_default_flag + flag;
        }

        if (object == null) {
            log(flag + " object == null");
            return;
        }

        MLog.dumpObjectAllFiled2(flag, object, object.getClass());
    }

    public static void dumpObjectAllFiled(String flag, Object object, Class cls) {
        if (flag == null) {
            flag = i_default_flag;
        }
        else {
            flag = i_default_flag + flag;
        }

        MLog.dumpObjectAllFiled2(flag, object, cls);
    }

    private static void log(String tag, String flag, String msg) {
        synchronized (LogUtils.class) {
            int length = 2000;
            if (msg.length() <= length) {
                Log.d(tag, flag + msg);
            } else {
                int idx = 0;
                while (idx < msg.length()) {
                    int endIdx = Math.min(msg.length(), idx + length);
                    String logmsg = msg.substring(idx, endIdx);
                    Log.d(tag, flag + logmsg);
                    idx = endIdx;
                }
            }
        }
    }

    public static void dumpRunningProcess(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
            log("------------------------------");
            log("running process count = " + list.size());
            int idx = 0;
            for (ActivityManager.RunningAppProcessInfo info : list) {
                log(String.format("info[%d] process = %s", idx++, info.processName));
            }
            log("------------------------------");
        }
        catch (Exception e) {
            log("error = " + e);
        }
    }

    public static void dumpTrack(String flag) {
        String text = " dump " + " ============= dumpTrack " + flag + " \n\b" + Log.getStackTraceString(new Throwable());

        log(text);
    }

    public static void dumpTrack() {
        String text = " dump " + " ============= dumpTrack \n\b" + Log.getStackTraceString(new Throwable());

        log(text);
    }
}
