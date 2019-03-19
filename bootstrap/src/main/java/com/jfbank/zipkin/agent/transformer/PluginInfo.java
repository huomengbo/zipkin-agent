package com.jfbank.zipkin.agent.transformer;

import java.util.List;

public interface PluginInfo {

    List<TransformTemplate> getTransformers();
}
