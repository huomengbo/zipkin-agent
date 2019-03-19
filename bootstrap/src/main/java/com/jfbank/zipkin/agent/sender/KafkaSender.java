package com.jfbank.zipkin.agent.sender;

import com.jfbank.zipkin.agent.common.Config;
import com.jfbank.zipkin.agent.entity.Span;
import com.jfbank.zipkin.agent.util.BytesEncoderUtil;
import com.jfbank.zipkin.agent.util.SpanUtil;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KafkaSender {

    private static Logger logger = LoggerFactory.getLogger(AsyncSpanSender.class);

    private static final String TOPIC = "zipkin";
    private static KafkaProducer<String, String> producer = null;
    private static KafkaSenderCallback callback = new KafkaSenderCallback();

    /*
       初始化生产者
     */
    static {
        producer = new KafkaProducer<String, String>(Config.getKafkaProps());
    }

    public static void sendList(List<Span> spanList) {
        List<zipkin2.Span> zipkin2List = SpanUtil.transform(spanList);
        byte[] result = BytesEncoderUtil.encode(zipkin2List);
        ProducerRecord record = new ProducerRecord<byte[], byte[]>(TOPIC, result);

        //发送消息
        producer.send(record, callback);

    }

    public static void send(Span body) {

        zipkin2.Span span = SpanUtil.transform(body);
        byte[] result = BytesEncoderUtil.encode(span);
        ProducerRecord record = new ProducerRecord<byte[], byte[]>(TOPIC, result);

        //发送消息
        producer.send(record, callback);
    }

    private static class KafkaSenderCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (null != exception) {
                logger.info("zipkin send error");
                logger.warn("zipkin span kafkaSender send error" + exception.getMessage());
            } else {
                logger.info("zipkin send success");
            }
        }
    }
}


