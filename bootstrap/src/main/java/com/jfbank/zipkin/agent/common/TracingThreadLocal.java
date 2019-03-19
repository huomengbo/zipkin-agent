package com.jfbank.zipkin.agent.common;

import com.jfbank.zipkin.agent.entity.Span;

/**
 **/
public class TracingThreadLocal {
    private static ThreadLocal<Span> local = new InheritableThreadLocal<>();

    public static Span get() {
        return local.get();
    }

    public static void set(Span span) {
        local.set(span);
    }

    public static void close() {
        local.remove();
    }


}
