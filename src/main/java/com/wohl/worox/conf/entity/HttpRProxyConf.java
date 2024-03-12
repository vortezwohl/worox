package com.wohl.worox.conf.entity;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class HttpRProxyConf {
    private Integer port;
    private Integer maxContentLength;
    private ConcurrentHashMap<String, String> bind;
}
