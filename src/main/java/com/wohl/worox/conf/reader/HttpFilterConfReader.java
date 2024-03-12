package com.wohl.worox.conf.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wohl.worox.conf.entity.HttpFilterConf;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import org.jentiti.annotation.Singleton;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Singleton
public class HttpFilterConfReader {
    private HttpFilterConf conf;
    @SuppressWarnings("all")
    public HttpFilterConf init() throws IOException, URISyntaxException {
        HttpFilterConf httpFilterConf = new HttpFilterConf();
        ObjectMapper objectMapper = new ObjectMapper();
        Path filterConf = Paths.get(System.getProperty("user.dir")+"\\worox.conf");
        if(!Files.exists(filterConf))
            filterConf = Paths.get(HttpRProxyConfReader.class.getClassLoader().getResource("worox.conf").toURI());
        String filterConfJson = null;
        if(Files.isRegularFile(filterConf) && Files.isReadable(filterConf)) {
            filterConfJson = "{"+Files.readString(filterConf, CharsetUtil.UTF_8).split("<http-filter>")[1].split("</http-filter>")[0]+"}";
            filterConfJson = filterConfJson.replace("methods","\"methods\"");
            filterConfJson = filterConfJson.replace("headers","\"headers\"");
            httpFilterConf = objectMapper.readValue(filterConfJson, HttpFilterConf.class);
        }
        conf = httpFilterConf;
        return httpFilterConf;
    }
}
