package com.jfbank.zipkin.agent.plugins.dubbo;

import com.jfbank.zipkin.agent.transformer.PluginInfo;
import com.jfbank.zipkin.agent.transformer.TransformTemplate;

import java.util.ArrayList;
import java.util.List;

public class DubboPlugin implements PluginInfo {

    private final String consumerClass = "com.alibaba.dubbo.rpc.cluster.support.AbstractClusterInvoker";
    private final String consumerMethod = "invoke";
    private final String consumerParam = "com.alibaba.dubbo.rpc.Invocation";

    private final String providerClass = "com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker";
    private final String providerMethod = "invoke";
    private final String providerParam = "com.alibaba.dubbo.rpc.Invocation";


    private List<TransformTemplate> transformTemplatesList = new ArrayList<>();

    public DubboPlugin() {
        TransformTemplate consumer = new TransformTemplate();
        consumer.setClassName(consumerClass);
        consumer.setMethodName(consumerMethod);
        consumer.setParam(consumerParam);
        consumer.setInterceptorClassName("com.jfbank.zipkin.agent.plugins.dubbo.filter.ClientInterceptor");
        transformTemplatesList.add(consumer);
        TransformTemplate provider = new TransformTemplate();
        provider.setClassName(providerClass);
        provider.setMethodName(providerMethod);
        provider.setParam(providerParam);
        provider.setInterceptorClassName("com.jfbank.zipkin.agent.plugins.dubbo.filter.ServerInterceptor");
        transformTemplatesList.add(provider);

    }

    @Override
    public List<TransformTemplate> getTransformers() {
        return transformTemplatesList;
    }

}
