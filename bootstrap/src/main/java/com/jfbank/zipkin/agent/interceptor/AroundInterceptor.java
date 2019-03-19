package com.jfbank.zipkin.agent.interceptor;

/**
 * 拦截抽象
 **/
public abstract class AroundInterceptor {

    /**
     * 开始之前调用
     *
     * @param param target
     */
    public abstract void preHandle(Object param);


    /**
     * 结束之后调用
     *
     *
     * @param param
     */
    public abstract void afterCompletion(Object param);

}
