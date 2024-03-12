package com.wohl.worox;

import com.wohl.worox.conf.entity.HttpRProxyConf;
import com.wohl.worox.conf.reader.HttpFilterConfReader;
import com.wohl.worox.conf.reader.HttpRProxyConfReader;
import com.wohl.worox.context.JentitiContext;
import com.wohl.worox.util.RProxy;
import com.wohl.worox.util.log.Logger;
import io.netty.bootstrap.ServerBootstrap;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        initRProxyBind();
        HttpRProxyConf rProxyConf = ((HttpRProxyConfReader)JentitiContext.get(HttpRProxyConfReader.class)).getConf();
        RProxy proxy = new RProxy();
        proxy.setPort(rProxyConf.getPort());
        proxy.setMaxContentLength(rProxyConf.getMaxContentLength());
        ServerBootstrap serverBootstrap = proxy.start();
    }

    private static void initRProxyBind() throws IOException, URISyntaxException {
        HttpRProxyConfReader httpRProxyConfReader = (HttpRProxyConfReader) JentitiContext.get(HttpRProxyConfReader.class);
        HttpFilterConfReader httpFilterConfReader = (HttpFilterConfReader) JentitiContext.get(HttpFilterConfReader.class);
        Logger logger = new Logger(httpRProxyConfReader);
        logger.info("Initialized HTTP reverse proxy "+ httpRProxyConfReader.init());
        logger = new Logger(httpFilterConfReader);
        logger.info("Initialized HTTP filter "+ httpFilterConfReader.init());
    }
}
