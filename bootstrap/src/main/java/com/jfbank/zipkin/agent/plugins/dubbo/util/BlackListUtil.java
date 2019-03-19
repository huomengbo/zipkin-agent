package com.jfbank.zipkin.agent.plugins.dubbo.util;

public class BlackListUtil {

    /**
     * 黑名单
     */
    private static String black = "com.alibaba.dubbo.monitor.MonitorService";


    public static boolean check(String interfaceName) {
        if (black.endsWith(interfaceName)) {
            return false;
        }
        return true;
    }

}
