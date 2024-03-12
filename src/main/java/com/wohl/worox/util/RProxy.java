package com.wohl.worox.util;

import com.wohl.worox.handler.FilterHandler;
import com.wohl.worox.handler.RProxyHandler;
import com.wohl.worox.util.log.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URISyntaxException;

@Data
public class RProxy {
    private ServerBootstrap serverBootstrap;
    private Integer port;
    private Integer maxContentLength;
    private Logger logger = new Logger(this);

    public RProxy() throws IOException, URISyntaxException {
        port = 80;
        maxContentLength = Integer.MAX_VALUE;
        serverBootstrap = new ServerBootstrap();
    }

    public RProxy(Integer port, Integer maxContentLength) throws IOException, URISyntaxException {
        this.port = port;
        this.maxContentLength = maxContentLength;
        serverBootstrap = new ServerBootstrap();
    }

    @SneakyThrows
    public ServerBootstrap start() {
        serverBootstrap
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new ChunkedWriteHandler());
                        channel.pipeline().addLast(new HttpServerCodec());
                        channel.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
                        channel.pipeline().addLast(new HttpContentCompressor());
                        channel.pipeline().addLast(new FilterHandler());
                        channel.pipeline().addLast(new RProxyHandler());
                    }
                })
                .bind(port)
                .sync()
                .addListener(future -> logger.info("HTTP reverse proxy initialized (port="+port+" max-content-length="+maxContentLength+")"));
        return serverBootstrap;
    }
}
