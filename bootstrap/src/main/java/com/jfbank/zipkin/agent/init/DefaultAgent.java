package com.jfbank.zipkin.agent.init;

import com.jfbank.zipkin.agent.sender.AsyncSpanSender;

public class DefaultAgent implements Agent {


    private AsyncSpanSender sender = new AsyncSpanSender();

    @Override
    public void start() {
        sender.start();
    }

    @Override
    public void stop() {
        sender.stop();
    }
}
