package com.jfbank.zipkin.agent.util;

import com.jfbank.zipkin.agent.entity.Annotation;
import com.jfbank.zipkin.agent.entity.Endpoint;
import com.jfbank.zipkin.agent.entity.Span;
import com.jfbank.zipkin.agent.entity.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpanUtil {


    /**
     * 获取traceId
     *
     * @return
     */
    public static String newTraceId() {

        String uuid = UUID.randomUUID().toString();
        String traceId = uuid.replace("-", "");
        traceId = traceId.substring(0, 16);
        return traceId;
    }

    public static List<zipkin2.Span> transform(List<Span> spanList) {
        List<zipkin2.Span> zipkin2List = new ArrayList<>();
        for (Span span : spanList) {
            zipkin2List.add(transform(span));
        }
        return zipkin2List;
    }


    /**
     * agent收集的span数据 => zipkin2.Span
     *
     * @param span
     * @return
     */
    public static zipkin2.Span transform(Span span) {

        zipkin2.Span.Builder builder = zipkin2.Span.newBuilder().traceId(span.getTraceId())
                .id(span.getId())
                .parentId(span.getParentId())
                .kind(transKind(span.getKind()))
                .name(span.getName())
                .timestamp(span.getTimestamp())
                .duration(span.getDuration())
                .localEndpoint(transEndpoint(span.getLocalEndpoint()))
                .remoteEndpoint(transEndpoint(span.getRemoteEndpoint()));

        List<Annotation> annotationList = span.getAnnotations();
        if (annotationList.size() > 0) {
            for (Annotation annotation : annotationList) {
                builder.addAnnotation(annotation.getTimestamp(), annotation.getValue());
            }
        }
        List<Tag> tagList = span.getTags();
        if (tagList.size() > 0) {
            for (Tag tag : tagList) {
                builder.putTag(tag.getKey(), tag.getValue());
            }
        }
        zipkin2.Span zSpan = builder.build();
        return zSpan;
    }


    private static zipkin2.Span.Kind transKind(Span.Kind kind) {
        switch (kind) {
            case CLIENT:
                return zipkin2.Span.Kind.CLIENT;
            case SERVER:
                return zipkin2.Span.Kind.SERVER;
            case CONSUMER:
                return zipkin2.Span.Kind.CONSUMER;
            case PRODUCER:
                return zipkin2.Span.Kind.PRODUCER;
            default:
                return null;
        }
    }

    private static zipkin2.Endpoint transEndpoint(Endpoint endpoint) {

        return zipkin2.Endpoint.newBuilder()
                .ip(endpoint.getIp())
                .serviceName(endpoint.getServiceName())
                .port(endpoint.getPort())
                .build();
    }

}
