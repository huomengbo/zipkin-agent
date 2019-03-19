package com.jfbank.zipkin.agent.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SpanConstant {
    public static final String CLIENT_SEND = "cs";//Client Send
    public static final String CLIENT_RECV = "cr";//Client Receive
    public static final String SERVER_SEND = "ss";//Server Send
    public static final String SERVER_RECV = "sr";//Server Receive
    public static final String MESSAGE_SEND = "ms";
    public static final String MESSAGE_RECV = "mr";
    public static final String WIRE_SEND = "ws";
    public static final String WIRE_RECV = "wr";
    public static final String CLIENT_SEND_FRAGMENT = "csf";
    public static final String CLIENT_RECV_FRAGMENT = "crf";
    public static final String SERVER_SEND_FRAGMENT = "ssf";
    public static final String SERVER_RECV_FRAGMENT = "srf";
    public static final String LOCAL_COMPONENT = "lc";
    public static final String ERROR = "error";
    public static final String CLIENT_ADDR = "ca";
    public static final String SERVER_ADDR = "sa";
    public static final String MESSAGE_ADDR = "ma";
    public static final List<String> CORE_ANNOTATIONS = Collections.unmodifiableList(Arrays.asList("cs", "cr", "ss", "sr", "ws", "wr", "csf", "crf", "ssf", "srf"));

    private SpanConstant() {
    }
}