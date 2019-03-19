
package com.jfbank.zipkin.agent.common;

import java.io.*;
import java.util.Properties;


/**
 * 加载配置文件.
 */
public class Config {

    public static final String DEFAULT_ENCODING = "UTF-8";
    //kafka配置
    private static Properties kafkaProps = new Properties();
    //服务名称
    private static String serviceName;

    public static String getServiceName() {
        return serviceName;
    }

    public static void setServiceName(String name) {
        serviceName = name;
    }


    /**
     * 获取kafka配置
     *
     * @return
     */
    public static Properties getKafkaProps() {
        return kafkaProps;
    }

    /**
     * 加载kafka配置
     *
     * @param kafkaFilePath
     * @throws IOException
     */
    public static void loadKafkaProperty(final String kafkaFilePath) throws Exception {
        if (kafkaFilePath == null) {
            throw new NullPointerException("kafkaFilePath must not is null");
        }
        InputStream in = null;
        Reader reader = null;
        try {
            in = new FileInputStream(kafkaFilePath);
            reader = new InputStreamReader(in, DEFAULT_ENCODING);
            kafkaProps.load(reader);
        } finally {
            close(reader);
            close(in);
        }
        if (kafkaProps.size() == 0) {
            throw new Exception("kafka config must not is null");
        }
    }


    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
                // skip
            }
        }
    }


}
