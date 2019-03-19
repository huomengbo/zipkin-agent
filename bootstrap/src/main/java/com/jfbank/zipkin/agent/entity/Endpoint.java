package com.jfbank.zipkin.agent.entity;

public class Endpoint {


    private String serviceName;
    private String ip;
    private Integer port;

    public Endpoint(String serviceName, String ip, Integer port) {
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


}
