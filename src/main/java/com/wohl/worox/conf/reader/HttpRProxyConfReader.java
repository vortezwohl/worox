package com.wohl.worox.conf.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wohl.worox.conf.entity.HttpRProxyConf;
import io.netty.util.CharsetUtil;
import lombok.Data;
import org.jentiti.annotation.Singleton;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Singleton
public class HttpRProxyConfReader {
    private HttpRProxyConf conf;
    @SuppressWarnings("all")
    public HttpRProxyConf init() throws IOException, URISyntaxException {
        HttpRProxyConf httpRProxyConf = new HttpRProxyConf();
        ObjectMapper objectMapper = new ObjectMapper();
        Path rproxyConf = Paths.get(System.getProperty("user.dir")+"\\worox.conf");
        if(!Files.exists(rproxyConf))
            rproxyConf = Paths.get(HttpRProxyConfReader.class.getClassLoader().getResource("worox.conf").toURI());
        String rproxyConfJson = null;
        if(Files.isRegularFile(rproxyConf) && Files.isReadable(rproxyConf)) {
            rproxyConfJson = "{"+
                    Files.readString(rproxyConf, CharsetUtil.UTF_8)
                    .split("<reverse-proxy>")[1]
                    .split("</reverse-proxy>")[0]
                    .replace("port","\"port\"")
                    .replace("max-content-length","\"maxContentLength\"")
                    .replace("bind","\"bind\"")
                    +"}";
            httpRProxyConf = objectMapper.readValue(rproxyConfJson, HttpRProxyConf.class);
        }
        conf = httpRProxyConf;
        return httpRProxyConf;
    }
}
