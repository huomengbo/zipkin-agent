package com.jfbank.zipkin.agent.plugins.dubbo.filter;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.RpcContext;
import com.jfbank.zipkin.agent.common.Config;
import com.jfbank.zipkin.agent.common.TraceConstant;
import com.jfbank.zipkin.agent.common.TracingThreadLocal;
import com.jfbank.zipkin.agent.entity.Endpoint;
import com.jfbank.zipkin.agent.entity.Span;
import com.jfbank.zipkin.agent.interceptor.AroundInterceptor;
import com.jfbank.zipkin.agent.plugins.dubbo.util.InvocationUtil;
import com.jfbank.zipkin.agent.util.DateUtil;
import com.jfbank.zipkin.agent.util.SpanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * dubbo服务端拦截器
 **/
public class ServerInterceptor extends AroundInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ClientInterceptor.class);

    @Override
    public void preHandle(Object param) {
        try {
            Invocation invocation = (Invocation) param;
            Map<String, String> at = invocation.getAttachments();

            Span span = new Span();
            String ParentId = at.get(TraceConstant.SPAN_ID_NAME);
            String traceId = at.get(TraceConstant.TRACE_ID_NAME);
            if (traceId != null) {
                span.setTraceId(traceId);
                span.setParentId(ParentId);
            } else {
                span.setTraceId(SpanUtil.newTraceId());
            }
            span.setId(SpanUtil.newTraceId());
            span.setKind(Span.Kind.SERVER);
            span.setName(invocation.getMethodName());
            span.setTimestamp(DateUtil.getCurrentMicrosecond());
            String applicationName = Config.getServiceName();
            span.setLocalEndpoint(new Endpoint(applicationName, RpcContext.getContext().getLocalHost(), null));
            span.setRemoteEndpoint(new Endpoint(null, RpcContext.getContext().getRemoteHost(), RpcContext.getContext().getRemotePort()));

            //            span.annotate(DateUtil.getCurrentMicrosecond(), SpanConstant.SERVER_RECV);
            TracingThreadLocal.set(span);
        } catch (Exception ex) {
            logger.error("dubbo服务端拦截器 preHandle 执行异常", ex);
        }

    }

    @Override
    public void afterCompletion(Object param) {
        try {
            Span span = TracingThreadLocal.get();
            if (span == null) {
                return;
            }
            Invocation invocation = (Invocation) param;
            span.tag("rpc.methodName", invocation.getMethodName());
            span.tag("rpc.arguments", InvocationUtil.getArguments(invocation));
            span.tag("rpc.interface", InvocationUtil.getInterface(invocation));
            span.tag("rpc.version", InvocationUtil.getVersion(invocation));
            span.tag("rpc.timeout", InvocationUtil.getTimeout(invocation));

//            span.annotate(DateUtil.getCurrentMicrosecond(), SpanConstant.SERVER_SEND);
            span.finish();
            TracingThreadLocal.close();
        } catch (Exception ex) {
            logger.error("dubbo客户端拦截器 afterCompletion 执行异常", ex);
        }

    }
}
