package com.jfbank.zipkin.agent.transformer;

public class TransformTemplate {

    //类全路径名称
    private String className;
    //方法名称简称
    private String methodName;
    //参数包装类的简称，获取该参数后会发送到拦截器的param里
    private String param;
    //拦截器全路径名称
    private String interceptorClassName;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getInterceptorClassName() {
        return interceptorClassName;
    }

    public void setInterceptorClassName(String interceptorClassName) {
        this.interceptorClassName = interceptorClassName;
    }

    public boolean check() {
        if (className == null) {
            return false;
        }
        if (methodName == null) {
            return false;
        }
        if (param == null) {
            return false;
        }
        if (interceptorClassName == null) {
            return false;
        }
        return true;
    }
}
