package com.jfbank.zipkin.agent.transformer;

import com.jfbank.zipkin.agent.util.MethodUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.MethodInfo;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTransformer implements ClassFileTransformer {

    /**
     * className：类名称
     * transformInfo：封装方法信息
     */
    public final Map<String, TransformTemplate> classNames = new HashMap<String, TransformTemplate>();

    public void addPlugin(PluginInfo PluginInfo) {
        List<TransformTemplate> transformTemplates = PluginInfo.getTransformers();
        if (transformTemplates != null && transformTemplates.size() > 0) {
            for (TransformTemplate transform : transformTemplates) {
                if (transform.check()) {
                    classNames.put(transform.getClassName(), transform);
                }
            }
        }
    }


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return null;
        }
        className = className.replace("/", ".");
        if (!classNames.containsKey(className)) {
            return null;
        }
        TransformTemplate transformTemplate = classNames.get(className);
        try {
            ClassPool pool = ClassPool.getDefault();
            String interceptorName = transformTemplate.getInterceptorClassName();
            //增加拦截器包的引用
            pool.importPackage(interceptorName);
            // 使用全称,用于取得字节码类<使用javassist>
            CtClass ctclass = pool.get(className);
            //增加拦截器类字段
            String sInterceptorName = interceptorName.substring(interceptorName.lastIndexOf(".") + 1, interceptorName.length());
            CtField field = CtField.make(" public " + sInterceptorName + " interceptor = new " + sInterceptorName + "();", ctclass);
            ctclass.addField(field);
            // 得到这方法实例
            CtMethod ctmethod = ctclass.getDeclaredMethod(transformTemplate.getMethodName());
            //获取参数下标
            MethodInfo methodInfo = ctmethod.getMethodInfo();
            String index = MethodUtil.getParamIndex(methodInfo.getDescriptor(), transformTemplate.getParam());
            //执行方法前走的拦截器方法
            ctmethod.insertBefore("{ interceptor.preHandle($" + index + "); }");
            //执行方法后走的拦截器方法
            ctmethod.insertAfter("{ interceptor.afterCompletion($" + index + "); }");
            return ctclass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
