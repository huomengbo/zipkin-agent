package com.jfbank.zipkin.agent.plugins.dubbo.filter;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.RpcContext;
import com.jfbank.zipkin.agent.common.Config;
import com.jfbank.zipkin.agent.common.TraceConstant;
import com.jfbank.zipkin.agent.common.TracingThreadLocal;
import com.jfbank.zipkin.agent.entity.Endpoint;
import com.jfbank.zipkin.agent.entity.Span;
import com.jfbank.zipkin.agent.interceptor.AroundInterceptor;
import com.jfbank.zipkin.agent.plugins.dubbo.util.BlackListUtil;
import com.jfbank.zipkin.agent.plugins.dubbo.util.InvocationUtil;
import com.jfbank.zipkin.agent.util.DateUtil;
import com.jfbank.zipkin.agent.util.SpanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * dubbo客户端拦截
 **/
public class ClientInterceptor extends AroundInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ClientInterceptor.class);

    @Override
    public void preHandle(Object param) {
        try {
            Invocation invocation = (Invocation) param;
            Map<String, String> at = invocation.getAttachments();

            Span span = TracingThreadLocal.get();
            //从此开始记录追踪
            if (span == null) {
                span = new Span();
                String spanId = SpanUtil.newTraceId();
                span.setId(spanId);
                span.setTraceId(spanId);
                span.setKind(Span.Kind.CLIENT);
                span.setName(invocation.getMethodName());
                span.setTimestamp(DateUtil.getCurrentMicrosecond());
            }

            span.setLocalEndpoint(new Endpoint(Config.getServiceName(), RpcContext.getContext().getLocalHost(), null));
            span.setRemoteEndpoint(new Endpoint(null, RpcContext.getContext().getRemoteHost(), RpcContext.getContext().getRemotePort()));

//            span.annotate(DateUtil.getCurrentMicrosecond(), SpanConstant.CLIENT_SEND);
            TracingThreadLocal.set(span);

            at.put(TraceConstant.SPAN_ID_NAME, span.getId());
            at.put(TraceConstant.TRACE_ID_NAME, span.getTraceId());

        } catch (Exception ex) {
            logger.error("dubbo客户端拦截器 preHandle 执行异常", ex);
        }
    }

    @Override
    public void afterCompletion(Object param) {

        Invocation invocation = (Invocation) param;
        //过滤黑名单的类
        if (!BlackListUtil.check(InvocationUtil.getInterface(invocation))) {
            return;
        }
        try {
            Span span = TracingThreadLocal.get();
            if (span == null) {
                return;
            }
            span.tag("rpc.methodName", invocation.getMethodName());
            span.tag("rpc.arguments", InvocationUtil.getArguments(invocation));
            span.tag("rpc.interface", InvocationUtil.getInterface(invocation));
            span.tag("rpc.version", InvocationUtil.getVersion(invocation));
            span.tag("rpc.timeout", InvocationUtil.getTimeout(invocation));

//            span.annotate(DateUtil.getCurrentMicrosecond(), SpanConstant.CLIENT_RECV);
            span.finish();
            TracingThreadLocal.close();
        } catch (Exception ex) {
            logger.error("dubbo客户端拦截器 afterCompletion 执行异常", ex);
        }

    }


}
