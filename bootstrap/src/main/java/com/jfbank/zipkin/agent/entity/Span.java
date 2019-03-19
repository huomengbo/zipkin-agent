package com.jfbank.zipkin.agent.entity;

import com.jfbank.zipkin.agent.sender.AsyncSpanSender;
import com.jfbank.zipkin.agent.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次跟踪记录
 * 时间单位：微妙
 */
public class Span {

    private String id;
    private String traceId;
    private String parentId;
    private long duration; //持续时间，即span的创建到span完成最终的采集所经历的时间，除去span自己逻辑处理的时间
    private Endpoint remoteEndpoint;
    private Endpoint localEndpoint;
    private List<Annotation> annotations = new ArrayList<>();//基本标注列表
    private long timestamp;  //span的创建时间
    private Kind kind;
    private String name;  //span的名称，一般是方法名称
    private List<Tag> tags = new ArrayList<>();


    public void finish() {
        this.duration = DateUtil.getCurrentMicrosecond() - this.timestamp;
        AsyncSpanSender.send(this);
    }

    public void tag(String key, String value) {
        tags.add(new Tag(key, value));
    }

    public void annotate(long timestamp, String value) {
        annotations.add(new Annotation(timestamp, value));
    }

    public enum Kind {
        CLIENT,
        SERVER,
        PRODUCER,
        CONSUMER;

        private Kind() {
        }
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Endpoint getRemoteEndpoint() {
        return remoteEndpoint;
    }

    public void setRemoteEndpoint(Endpoint remoteEndpoint) {
        this.remoteEndpoint = remoteEndpoint;
    }

    public Endpoint getLocalEndpoint() {
        return localEndpoint;
    }

    public void setLocalEndpoint(Endpoint localEndpoint) {
        this.localEndpoint = localEndpoint;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }
}
