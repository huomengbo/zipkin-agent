package com.jfbank.zipkin.agent;

import com.jfbank.zipkin.agent.common.AgentDirClassPathResolver;
import com.jfbank.zipkin.agent.common.Config;
import com.jfbank.zipkin.agent.common.TraceConstant;
import com.jfbank.zipkin.agent.init.Agent;
import com.jfbank.zipkin.agent.init.DefaultAgent;
import com.jfbank.zipkin.agent.plugins.dubbo.DubboPlugin;
import com.jfbank.zipkin.agent.transformer.MyTransformer;

import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.jar.JarFile;

public class Bootstrap {

//    private static final Logger logger = LoggerFactory.getLogger(AgentDirClassPathResolver.class);

    public static void premain(String args, Instrumentation instrumentation) throws Exception {
//        logger.info("zipkin agent 开始装载，方法 premain 参数：{}", args);
        System.out.println("=========start======");

        AgentDirClassPathResolver classPathResolver = new AgentDirClassPathResolver();
        //加载项目配置到全局配置里
        appendConfigToProperties(args, classPathResolver.getKafkaConfigPath());
        //加载类到项目里
        appendToBootstrapClassLoader(instrumentation, classPathResolver.getLibJarList());

        MyTransformer transformer = new MyTransformer();
        //注册agent
        transformer.addPlugin(new DubboPlugin());
        instrumentation.addTransformer(transformer);

        Agent Agent = new DefaultAgent();
        Agent.start();
        registerShutdownHook(Agent);
//        logger.info("zipkin agent load success");
    }

    public static void registerShutdownHook(Agent Agent) {
        Thread thread = new Thread() {
            @Override
            public void run() {
//                logger.info("zipkin agent stop.");
                Agent.stop();
            }
        };
        Runtime.getRuntime().addShutdownHook(thread);
    }

    /**
     * 加载配置文件
     *
     * @param args
     * @param configPath
     * @throws Exception
     */
    private static void appendConfigToProperties(String args, String configPath) throws Exception {
        Config.loadKafkaProperty(configPath);

        Config.setServiceName(getServiceName(args));
    }

    /**
     * 加载jar到项目中
     *
     * @param instrumentation
     * @param jarFileList
     */
    private static void appendToBootstrapClassLoader(Instrumentation instrumentation, List<JarFile> jarFileList) {
        for (JarFile jarFile : jarFileList) {
//            logger.info("appendToBootstrapClassLoader: " + jarFile.getName());
            instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
        }
    }

    /**
     * 从入参里获取服务名称
     *
     * @param args
     * @return
     */
    private static String getServiceName(String args) {
        if (args != null) {
            String[] params = args.split(",");

            for (String param : params) {
                String[] pair = param.split("=");
                if (TraceConstant.ZIP_CONF_NAME.equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return "UNKNOW";
    }

}
