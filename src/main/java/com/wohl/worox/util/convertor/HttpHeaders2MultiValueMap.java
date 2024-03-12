package com.wohl.worox.util.convertor;

import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class HttpHeaders2MultiValueMap {
    public static MultiValueMap<String, String> convert(HttpHeaders httpHeaders) {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> header : httpHeaders)
            result.add(header.getKey(),header.getValue());
        return result;
    }
}
