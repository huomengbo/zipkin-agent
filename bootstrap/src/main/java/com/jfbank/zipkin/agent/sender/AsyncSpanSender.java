package com.jfbank.zipkin.agent.sender;

import com.jfbank.zipkin.agent.common.Config;
import com.jfbank.zipkin.agent.entity.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 异步发送span数据
 **/
public class AsyncSpanSender {

    private static Logger logger = LoggerFactory.getLogger(AsyncSpanSender.class);

    public static final int SEND_ITEM_INTEVAL = 100;//mills
    public static final int MAX_SEND_ITEM_PER_WRITE = 100;
    public static final int SPAN_MAX_HOLD = 1024 * 5;
    private static BlockingQueue<Span> queue = new LinkedBlockingQueue<Span>();
    private Thread t;
    private boolean running;


    public static void send(Span span) {
        queue.offer(span);

    }

    public void start() {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (running && !Thread.currentThread().isInterrupted()) {
                        if (queue.peek() == null) {
                            try {
                                Thread.sleep(SEND_ITEM_INTEVAL + (int) (Math.random() * SEND_ITEM_INTEVAL));
                            } catch (InterruptedException e) {
                                logger.error("asyncSpanSender-thread is interrupted,then the sender-thread will exit.", e);
                                break;
                            }
                        }

                        //保证持有的item不超过阀值，防止因产生过快、消耗过慢、连接失败等原因造成的内存泄露
                        while (queue.size() > SPAN_MAX_HOLD) {
                            queue.poll();
                        }
                        //期望在没有连接时能暂存数据，所以不使用take，take会出队列
                        if (null != queue.peek()) {
                            try {
                                List<Span> list = new ArrayList<Span>();
                                for (int i = 0; i < SPAN_MAX_HOLD; i++) {
                                    Span s = queue.poll();
                                    if (null == s) {
                                        break;
                                    }
                                    list.add(s);
                                    if (list.size() >= MAX_SEND_ITEM_PER_WRITE) {
                                        KafkaSender.sendList(list);
                                        list.clear();
                                    }
                                }
                                if (list.size() > 0) {
                                    KafkaSender.sendList(list);
                                }
                            } catch (Exception e) {
                                logger.error("asyncSpanSender send span exception ", e);
                                continue;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("asyncSpanSender exit.", e);
                }
            }
        });
        t.setName("asyncSpanSender for app[" + Config.getServiceName() + "]");
        //t.setDaemon(true);
        running = true;
        t.start();
        logger.info("asyncSpanSender start.");
    }


    public void stop() {
        running = false;
        if (null != t) {
            t.interrupt();
            t = null;
        }
        if (null != queue) {
            queue.clear();
            queue = null;//help gc
        }
        logger.info("asyncSpanSender stop.");
    }


}
