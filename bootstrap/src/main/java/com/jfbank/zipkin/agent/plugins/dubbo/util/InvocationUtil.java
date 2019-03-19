package com.jfbank.zipkin.agent.plugins.dubbo.util;

import com.alibaba.dubbo.rpc.Invocation;

public class InvocationUtil {


    public static String getArguments(Invocation invocation) {
        Object[] arguments = invocation.getArguments();
        String param = "";
        if (arguments != null && arguments.length > 0) {
            for (Object argument : arguments) {
                param += argument.toString() + ",";
            }
        }
        return param;
    }

    public static String getInterface(Invocation invocation) {
        return invocation.getAttachment("interface");
    }

    public static String getVersion(Invocation invocation) {
        return invocation.getAttachment("version");
    }


    public static String getTimeout(Invocation invocation) {
        return invocation.getAttachment("timeout");
    }
}
