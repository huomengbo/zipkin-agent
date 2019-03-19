package com.jfbank.zipkin.agent.util;

public class DateUtil {

    /**
     * 获取当前微秒时间
     *
     * @return
     */
    public static long getCurrentMicrosecond() {
        Long cutime = System.currentTimeMillis() * 1000; // 微秒
        Long nanoTime = System.nanoTime(); // 纳秒
        return cutime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
    }

}
