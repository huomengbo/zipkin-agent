package com.jfbank.zipkin.agent.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

public class AgentDirClassPathResolver {

//    private static final Logger logger = LoggerFactory.getLogger(AgentDirClassPathResolver.class);

    private String agentJarName = "bootstrap-1.0.jar";
    private String agentDirPath;

    public AgentDirClassPathResolver() throws Exception {

        String agentJarFullPath = parseAgentJarPath(getClassPathFromSystemProperty(), agentJarName);
        if (agentJarFullPath == null) {
            throw new Exception("agentJarFullPath is null");
        }
        String agentDirPath = parseAgentDirPath(agentJarFullPath);
        if (agentDirPath == null) {
            throw new Exception("agentDirPath is null");
        }
        this.agentDirPath = agentDirPath;
    }

    /**
     * 获取lib下的所有jar
     *
     * @return
     */
    public List<JarFile> getLibJarList() {
        String agentLibPath = getAgentLibPath(this.agentDirPath);
        File libDir = new File(agentLibPath);
        if (!libDir.exists()) {
//            logger.warn(agentLibPath + " not found");
            return Collections.emptyList();
        }
        if (!libDir.isDirectory()) {
//            logger.warn(agentLibPath + " not Directory");
            return Collections.emptyList();
        }
        final List<JarFile> jarFiles = new ArrayList<JarFile>();

        final File[] findJarList = findJar(libDir);
        if (findJarList != null) {
            for (File file : findJarList) {
                try {
                    JarFile jarFile = new JarFile(file);
                    if (jarFile != null) {
                        jarFiles.add(jarFile);
                    }
                } catch (IOException e) {
//                    logger.info("load {} fail ", file.getName(), e.getMessage());
                    continue;
                }
            }
        }
        return jarFiles;
    }

    public String getKafkaConfigPath() {
        return agentDirPath + File.separator + "kafka.config";
    }

    private String getClassPathFromSystemProperty() {
        return System.getProperty("java.class.path");
    }

    /**
     * 找到bootstrap-1.0.jar的path
     *
     * @param classPath
     * @param agentJar
     * @return
     */
    private String parseAgentJarPath(String classPath, String agentJar) {
        String[] classPathList = classPath.split(File.pathSeparator);
        for (String findPath : classPathList) {
            boolean find = findPath.contains(agentJar);
            if (find) {
                return findPath;
            }
        }
        return null;
    }

    private String parseAgentDirPath(String agentJarFullPath) {
        int index1 = agentJarFullPath.lastIndexOf("/");
        int index2 = agentJarFullPath.lastIndexOf("\\");
        int max = Math.max(index1, index2);
        if (max == -1) {
            return null;
        }
        return agentJarFullPath.substring(0, max);
    }

    private String getAgentLibPath(String agentDirPath) {
        return agentDirPath + File.separator + "lib";
    }


    /**
     * 过滤非jar文件
     *
     * @param libDir
     * @return
     */
    private File[] findJar(File libDir) {
        return libDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getName();
                if (path.lastIndexOf(".jar") != -1) {
                    return true;
                }
                return false;
            }
        });
    }

}
