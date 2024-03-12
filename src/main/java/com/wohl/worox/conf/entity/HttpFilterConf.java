package com.wohl.worox.conf.entity;

import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

@Data
public class HttpFilterConf {
    private ConcurrentLinkedDeque<String> methods;
    private Map<String, List<String>> headers;

    public ConcurrentLinkedDeque<String> getMethods() {
        return methods != null ? methods : new ConcurrentLinkedDeque<>();
    }

    public Map<String, List<String>> getHeaders() {
        return headers != null ? headers : new LinkedMultiValueMap<>();
    }
}
