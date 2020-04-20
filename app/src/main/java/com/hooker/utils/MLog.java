package com.hooker.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class MLog {

    public final static String TAGNAME = "hooker";
    private final static String TAG = " - " + TAGNAME + " - ";
    private final static String TAG2 = TAGNAME;

    public static void log(String msg) {
        log2(TAG, msg);
    }

    public static void logl(String msg) {
        log2(TAG, "- l - " + msg);
    }

    public static void dumpObjectFiled(Object object, Class clazz) {
        dumpObjectFiled2(TAG, object, clazz);
    }

    public static void dumpObjectAllFiled(Object object, Class clazz) {
        dumpObjectAllFiled2("", object, clazz);
    }

    public static void dumpTrack() {
        dumpTrack2(TAG, true);
    }

    public static void log2(String flag, String msg) {
        int length = 2000;
        String ext = TAG2;
        if (msg.length() <= length) {
            Log.d(ext, flag + msg);
        }
        else {
            int idx = 0;
            while (idx + length < msg.length()) {
                String logmsg = msg.substring(idx, idx + length);
                Log.d(ext, flag + logmsg);
                idx += length;
                if (idx + length >= msg.length()) {
                    logmsg = msg.substring(idx, msg.length());
                    Log.d(ext, flag + logmsg);
                    break;
                }
            }
        }
    }

    public static void dumpChildView(View rootView) {
//        if (!MData.isOpenDumpChildView()) return;
        dumpChildView(rootView, 0);

    }

    private static void dumpChildView(View rootView, int idx) {
        if (rootView == null) {
            MLog.log("is null");
            return;
        }

        String fmt = "";
        for (int i = 0; i < idx; i++) {
            fmt = fmt + "  ";
        }

        fmt += rootView.toString();

        if (rootView instanceof ViewGroup) {
            MLog.log(fmt);

            ViewGroup viewGroup = (ViewGroup)rootView;
            final int count = viewGroup.getChildCount();

            for (int i = 0; i < count; i++) {
                View view = viewGroup.getChildAt(i);
                dumpChildView(view, idx + 1);
            }
        }
        else {
            if (rootView instanceof TextView) {
                TextView textView = (TextView)rootView;
                if (textView.getText() == null) {
                    fmt += " --- text is null";
                }
                else {
                    fmt += "text: " + textView.getText().toString();
                }
            }

            fmt += "  ---------- is leaf";
            MLog.log(fmt);
        }
    }



    public static void dumpTrack2(String flag, boolean limitEnabled) {
        String text = " dump " + flag + " ============= dumpTrack \n\b" + Log.getStackTraceString(new Throwable());

        MLog.log2(flag, text);

    }


    private static final Pattern pattern = Pattern.compile("\\w+\\.");
    private static String removeQualifiers(String name) {
        return pattern.matcher(name).replaceAll("");
    }

    public static void dumpObjectFiled2(String flag, Object object, Class clazz) {
        if (clazz == null || object == null) return;

        MLog.log("dumpObjectFiled begin -------------------");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---\n\b======== class fields =========\n\b");
        stringBuilder.append(object.toString());
        stringBuilder.append("\n\bclass = ");
        stringBuilder.append(object.getClass().getName());
        stringBuilder.append("\n\b{");
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                stringBuilder.append("\n\t");
                int mod = field.getModifiers();
                stringBuilder.append((mod == 0) ? "" : (Modifier.toString(mod) + " "));
                stringBuilder.append(field.getType().getName());
                stringBuilder.append(" ");
                stringBuilder.append(field.getName());

                stringBuilder.append(" = ");

                try {
                    String name = field.getName();
//                    MLog.log("name = " + name);
                    field.setAccessible(true);
                    Object value = field.get(object);
//                    Object value = XposedHelpers.getObjectField(object, name);
                    stringBuilder.append(String.valueOf(value));
                }
                catch (Exception e) {}
            }
        }

//        MLog.log2(flag, "006");
        stringBuilder.append("\n\b}\n--");
        MLog.log2(flag, stringBuilder.toString());

        MLog.log("dumpObjectFiled end -------------------");
    }

    public static String getDumpObjectFiled(Object object) {
        if (object == null) return "";
        return getDumpObjectFiled(object, object.getClass());
    }

    public static String getDumpObjectFiled(Object object, Class clazz) {
        if (clazz == null || object == null) return "";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("== class fields ==\n\b");
        stringBuilder.append(object.toString());
        stringBuilder.append("\n\bclass = ");
        stringBuilder.append(object.getClass().getName());
        stringBuilder.append("\n\b{");
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            stringBuilder.append("\n\t");
            int mod = field.getModifiers();
            stringBuilder.append((mod == 0) ? "" : (Modifier.toString(mod) + " "));
            stringBuilder.append(field.getType().getName());
            stringBuilder.append(" ");
            stringBuilder.append(field.getName());

            stringBuilder.append(" = ");

            try {
                String name = field.getName();
                field.setAccessible(true);
                Object value = field.get(object);
                stringBuilder.append(String.valueOf(value));
            }
            catch (Exception e) {}
        }

        stringBuilder.append("\n\b}\n--");
        return stringBuilder.toString();
    }

    public static void dumpObjectAllFiled2(String flag, Object object, Class clazz) {
        if (clazz == null || object == null) {
            log(flag + " class == null 或 object == null");
            return;
        }
        flag = TAG + flag;

        MLog.log2(flag, "dumpObjectAllFiled2 begin -------------------");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---\n\b======== class fields =========\n\b");
        stringBuilder.append(object.toString());
        stringBuilder.append("\n\bclass = ");
        stringBuilder.append(object.getClass().getName());
        stringBuilder.append("\n\b{");
        Field[] declaredFields = clazz.getDeclaredFields();
        while (declaredFields != null) {
            for (Field field : declaredFields) {
                stringBuilder.append("\n\t");
                int mod = field.getModifiers();
                stringBuilder.append((mod == 0) ? "" : (Modifier.toString(mod) + " "));
                stringBuilder.append(field.getType().getName());
                stringBuilder.append(" ");
                stringBuilder.append(field.getName());

                stringBuilder.append(" = ");

                try {
                    String name = field.getName();
//                    MLog.log("name = " + name);
                    field.setAccessible(true);
                    Object value = field.get(object);
//                    Object value = XposedHelpers.getObjectField(object, name);
                    stringBuilder.append(String.valueOf(value));
                }
                catch (Exception e) {}
            }

            stringBuilder.append("\n\b---------\n");

            clazz = clazz.getSuperclass();
            if (clazz == null) {
                break;
            }

            declaredFields = clazz.getDeclaredFields();
        }

//        MLog.log2(flag, "006");
        stringBuilder.append("\n\b}\n--");
        MLog.log2(flag, stringBuilder.toString());

        MLog.log2(flag, "dumpObjectAllFiled2 end -------------------");
    }

    public static void dumpClassSuper(Class clazz) {
        dumpClassSuper2("ChkScene", clazz);
    }

    public static void dumpClassSuper2(String flag, Class clazz) {
        if (clazz == null) return;

        MLog.log2(flag, "dump super class ------ " + clazz.getName());
        StringBuilder sb = new StringBuilder();
        sb.append("\n\bthis = ");
        sb.append(clazz.toString());

        sb.append("\n\bsuper = {");
        Class superCls = clazz;
        try {
            while ((superCls = superCls.getSuperclass()) != null) {
                sb.append("\n\t");
                sb.append(superCls.getName());
            }
        } catch (Exception e) {
            sb.append("not know super class err:");
            sb.append(e.toString());
        }
        finally {
            sb.append("\n\b}");
            sb.append("\n----------------\n");
            MLog.log2(flag, sb.toString());
        }

    }

    public static void dumpClass(Class clazz) {
//        if (!MData.isOpenDumpClass()) return;

        if (clazz == null) return;

        MLog.log("dump class ------ " + clazz.getName());
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.toString());

        sb.append("\n====================\n\bsuper:");
        Class superCls = clazz;
        try {
            while ((superCls = superCls.getSuperclass()) != null) {
                sb.append("\n\t");
                sb.append(superCls.getName());

            }
        } catch (Exception e) {
            sb.append("\n\bnot know super class err:");
            sb.append(e.toString());
        }

        sb.append("\n\b----------------\n");

        MLog.log(sb.toString());
        sb.delete(0, sb.length());

        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields != null) {
            sb.append("\n\bdeclared fields:");
            for (Field f : declaredFields) {
                sb.append("\n\t");
                sb.append(removeQualifiers(f.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        // Methods
        Method[] declaredMethods = clazz.getDeclaredMethods();
        if (declaredMethods != null) {
            sb.append("\n\bdeclared methods:");
            for (Method m : declaredMethods) {
                sb.append("\n\t");
                sb.append(removeQualifiers(m.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        // Constructors
        Constructor<?>[] declaredConstructor = clazz.getDeclaredConstructors();
        if (declaredConstructor != null) {
            sb.append("\n\bdeclared constructors:");
            for (Constructor<?> c : declaredConstructor) {
                sb.append("\n\t");
                sb.append(removeQualifiers(c.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        // Inner classes
        Class<?>[] declaredInnerClasses = clazz.getDeclaredClasses();
        if (declaredInnerClasses != null) {
            sb.append("\n\bdeclared inner classes:");
            for (Class<?> c : declaredInnerClasses) {
                sb.append("\n\t");
                sb.append(removeQualifiers(c.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        // Interfaces
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            sb.append("\n\binterfaces:");
            for (Class<?> i : interfaces) {
                sb.append("\n\t");
                sb.append(removeQualifiers(i.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        // Enums
        Object[] enums = clazz.getEnumConstants();
        if (enums != null) {
            sb.append("\n\benums:");
            for (Object o : enums) {
                sb.append("\n\t");
                sb.append(removeQualifiers(o.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        Constructor<?>[] cons = clazz.getConstructors();
        if (cons != null) {
            sb.append("constructors:\n");
            for (Constructor<?> c : cons) {
                sb.append("\n\t");
                sb.append(removeQualifiers(c.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        Field[] fields = clazz.getFields();
        if (fields != null) {
            sb.append("fields:\n");
            for (Field f : fields) {
                sb.append("\n\t");
                sb.append(removeQualifiers(f.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }

        Method[] methods = clazz.getMethods();
        if (methods != null) {
            sb.append("methods:\n");
            for (Method m : methods) {
                sb.append("\n\t");
                sb.append(removeQualifiers(m.toString()));
            }

            sb.append("\n\b----------------\n");
            MLog.log(sb.toString());
            sb.delete(0, sb.length());
        }


        sb.append("inner classes:\n");
        Class<?>[] innerClasses = clazz.getDeclaredClasses();
        if (innerClasses != null) {
            for (Class<?> c : innerClasses) {
                sb.append("\n\t");
                sb.append(removeQualifiers(c.toString()));
            }

            sb.append("\n\b----------------\n");
        }

        sb.append("}");

        MLog.log(sb.toString());
        sb.delete(0, sb.length());

    }

    public static void dumpTid() {
        MLog.log("tid = " + Integer.toHexString(android.os.Process.myTid()));
    }

    public static void dumpUid() {
        MLog.log("uid = " + Integer.toHexString(android.os.Process.myUid()));
    }

    public static String getDumpArgs(Object[] args) {
        if (args == null) {
            return "args is empty";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("args[");
        builder.append(args.length);
        builder.append("] = (");
        if (args.length > 0) {
            builder.append(args[0]);
        }
        for (int i = 1; i < args.length; i++) {
            builder.append(", ");
            builder.append(args[i]);

        }

        builder.append(")");
//        MLog.log(builder.toString());
        return builder.toString();
    }

    public static String getDumpArgsType(Object[] args) {
        if (args == null) {
            return "args is empty";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("args[");
        builder.append(args.length);
        builder.append("] = (");
        if (args.length > 0) {
            if (args[0] == null) {
                builder.append("null");
            }
            else {
                builder.append(args[0].getClass().getName());
            }
        }
        for (int i = 1; i < args.length; i++) {
            builder.append(", ");
            if (args[i] == null) {
                builder.append("null");
            }
            else {
                builder.append(args[i].getClass().getName());
            }

        }

        builder.append(")");
//        MLog.log(builder.toString());
        return builder.toString();
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

    public static void dumpBundle(Bundle bundle, String flag) {
        if (bundle == null) {
            log(flag + " bundle == null");
            return;
        }

        synchronized (bundle) {
            try {
                Set<String> keyset = bundle.keySet();
                int size = keyset.size();
                log("size = " + size);
                int i = 0;
                for (String key : keyset) {
                    log(flag + " - [" + i + "] {" + key + " ==== " + bundle.get(key) + "}");
                    i++;
                }
            }
            catch (Exception e) {
                log("dump bundle error - " + flag + " = " + e);
            }
        }
    }

    public static void dumpCurrentActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        String activityName = (runningTaskInfo.get(0).topActivity).toString();
        log("current activity = " + activityName);
    }
}

