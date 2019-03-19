package com.jfbank.zipkin.agent.util;

public class MethodUtil {

    /**
     * 根据参数的描述获取参数下标
     * (Lcom/jfbank/microservice/client/User;Ljava/lang/String;)Ljava/lang/String; => Lcom.jfbank.microservice.client.User => 1
     *
     * @param desc
     * @param paramClass
     * @return
     */
    public static String getParamIndex(String desc, String paramClass) {
        desc = desc.substring(desc.indexOf("(") + 1, desc.indexOf(")"));
        desc = desc.replace("/", ".");
        String[] params = desc.split(";");
        int index = 1;
        for (String param : params) {
            if (param.endsWith(paramClass)) {
                return String.valueOf(index);
            }
            index++;
        }
        return "1";
    }
}
