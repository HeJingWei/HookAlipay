package com.hooker.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;


import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class AlipayTools {

    /**
     * 获取账单列表
     */

    public static String getBillList(ClassLoader classLoader) {
        try {
            String res = hookList(classLoader).toString();
            return TextUtils.isEmpty(res) ? "无账单列表" : res;
        } catch (Throwable e) {
            log("getBillList error = " + e);
            return "error = " + e;
        }
    }

    /**
     * 获取账单详情
     * <p>
     * 具体参数据实际情况添加
     */
    public static String getBillInfo(ClassLoader classLoader) {
        try {
            Object executeRPC = hookListInfo(classLoader, "2020041722001450911452205397");
            return executeRPC == null ? "无此订单详情" : executeRPC.toString();

        } catch (Throwable e) {
            log("getBillListDetail error = " + e);
            return "error = " + e;
        }
    }

    /**
     * 获取账单列表的方法
     *
     * @param classLoader
     * @return
     */
    private static Object hookList(ClassLoader classLoader) {
        Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", classLoader);
        Object instance = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");

        Object MicroApplicationContext = XposedHelpers.callMethod(instance, "getMicroApplicationContext");

        Class<?> rpcServiceClass = XposedHelpers.findClass("com.alipay.mobile.framework.service.common.RpcService", classLoader);
        Object rpcService = XposedHelpers.callMethod(MicroApplicationContext, "findServiceByInterface", rpcServiceClass.getName());

        Class<?> billListPBRPCServiceClass = XposedHelpers.findClass("com.alipay.mobilebill.biz.rpc.bill.v9.pb.BillListPBRPCService", classLoader);
        Object billListPBRPCService = XposedHelpers.callMethod(rpcService, "getPBRpcProxy", billListPBRPCServiceClass);
        Class QueryListReq = XposedHelpers.findClass("com.alipay.mobilebill.common.service.model.pb.QueryListReq", classLoader);
        Object o = XposedHelpers.newInstance(QueryListReq);
        Object queryRes = XposedHelpers.callMethod(billListPBRPCService, "query", o);
        return XposedHelpers.getObjectField(queryRes, "billListItems");
    }

    /**
     * 获取账单详情的方法
     *
     * @param classLoader
     * @return
     */
    private static Object hookListInfo(ClassLoader classLoader, String tradeNo) {
        Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", classLoader);
        Object instance = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");

        Object MicroApplicationContext = XposedHelpers.callMethod(instance, "getMicroApplicationContext");

        Class<?> rpcServiceClass = XposedHelpers.findClass("com.alipay.mobile.framework.service.common.RpcService", classLoader);
        Object rpcService = XposedHelpers.callMethod(MicroApplicationContext, "findServiceByInterface", rpcServiceClass.getName());

        Class<?> simpleRpcServiceClass = XposedHelpers.findClass("com.alipay.mobile.framework.service.ext.SimpleRpcService", classLoader);
        Object simpleRpcService = XposedHelpers.callMethod(rpcService, "getRpcProxy", simpleRpcServiceClass);

        Object executeRPC = XposedHelpers.callMethod(simpleRpcService, "executeRPC", "alipay.mobile.bill.QuerySingleBillDetailForH5",
                "[{\"bizType\":\"TRADE\",\"queryOrder\":false,\"tradeNo\":\"" + tradeNo + "\",\"useCardStyle\":\"\"}]",
                null);

        return executeRPC;
    }

    /**
     * 获取收款二维码
     * <p>
     * 具体参数据实际情况添加
     */
    public static String getQRCode(ClassLoader classLoader) {
        try {
            Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", classLoader);
            Object instance = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");

            Object MicroApplicationContext = XposedHelpers.callMethod(instance, "getMicroApplicationContext");

            Class<?> rpcServiceClass = XposedHelpers.findClass("com.alipay.mobile.framework.service.common.RpcService", classLoader);
            Object rpcService = XposedHelpers.callMethod(MicroApplicationContext, "findServiceByInterface", rpcServiceClass.getName());

            Class<?> collectMoneyRpcClass = XposedHelpers.findClass("com.alipay.transferprod.rpc.CollectMoneyRpc", classLoader);
            Object collectMoneyRpc = XposedHelpers.callMethod(rpcService, "getRpcProxy", collectMoneyRpcClass);

            Class CreateSessionReqClass = XposedHelpers.findClass("com.alipay.transferprod.rpc.req.CreateSessionReq", classLoader);
            Object createSessionReq = XposedHelpers.newInstance(CreateSessionReqClass);
            Object createSessionRes = XposedHelpers.callMethod(collectMoneyRpc, "createSession", createSessionReq);
            Object qrCodeUrl = XposedHelpers.getObjectField(createSessionRes, "qrCodeUrl");

            return qrCodeUrl.toString();
        } catch (Throwable e) {
            log("qrCode error = " + e);
            return "error = " + e;
        }

    }


    /**
     * 循环操作每笔账单
     *
     * @param classLoader
     */
    public static String getFilterList(ClassLoader classLoader) {
        //获取账单列表
        try {

            Object billListItems = hookList(classLoader);
            if (billListItems != null) {
                List bills = (List) billListItems;
                String res = "";
                for (int i = 0; i < bills.size(); i++) {
                    String filter = logFilterItem(bills.get(i), classLoader);
                    if (!TextUtils.isEmpty(filter)) {
                        res += filter;
                    }
                }
                return TextUtils.equals("", res) ? "三天内无新账单" : res;
            }
            return "三天内无新账单";
        } catch (Throwable e) {
            log("getFilterList error = " + e);
            return "error = " + e;
        }
    }

    /**
     * 打印三天内的账单详情
     *
     * @param obj
     * @param classLoader
     */
    private static String logFilterItem(Object obj, ClassLoader classLoader) {
        String filterStr = "";
        Object gmtCreate = XposedHelpers.getObjectField(obj, "gmtCreate");
        Long createTime = Long.valueOf(gmtCreate.toString());
        if (System.currentTimeMillis() - createTime < 3 * 24 * 60 * 60 * 1000) {
            Object executeRPC = hookListInfo(classLoader, XposedHelpers.getObjectField(obj, "bizInNo").toString());
            log("filter detail:" + executeRPC.toString());
            if (executeRPC != null) {
                JSONObject parse = JSONObject.parseObject(executeRPC.toString());
                String succ = parse.getString("succ");
                if (TextUtils.equals("true", succ)) {
                    filterStr = executeRPC.toString();
                }
            }
        }
        return filterStr;
    }

    /**
     * hook 支付宝RPC invoke入口
     *
     * @param classLoader
     */
    public static void getRpcRunnable(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.alipay.mobile.common.rpc.RpcInvocationHandler", classLoader, "invoke",
                Object.class, Method.class, Object[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Method method = (Method) param.args[1];
                        log("invoke  methodName：" + method.getName());
                        log("invoke  methodClass：" + method.getDeclaringClass().getName());
                        Object[] params = (Object[]) param.args[2];
                        if (params != null) {
                            for (int i = 0; i < params.length; i++) {
                                log("invoke  params：" + (params[i] == null ? "null" : params[i].toString()));
                            }
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("invoke  methodResult：" + (param.getResult() == null ? "" : param.getResult().toString()));
                        log("invoke  methodResultClass：" + (param.getResult() == null ? "" : param.getResult().getClass().getName()));
                    }
                });

    }

    private static void log(String msg) {
        LogUtils.log(msg);
    }
}
