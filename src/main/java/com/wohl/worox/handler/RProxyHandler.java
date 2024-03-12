package com.wohl.worox.handler;

import com.wohl.worox.conf.reader.HttpRProxyConfReader;
import com.wohl.worox.context.JentitiContext;
import com.wohl.worox.util.convertor.HttpHeaders2MultiValueMap;
import com.wohl.worox.util.log.Logger;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

public class RProxyHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = new Logger(this);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> bind = ((HttpRProxyConfReader) JentitiContext.get(HttpRProxyConfReader.class)).getConf().getBind();
        long timestampWhenStart = System.currentTimeMillis();
        FullHttpRequest request = (FullHttpRequest) msg;
        logger.info("HTTP request received from client > version="+ request.protocolVersion() +" method="+request.method()+" path="+request.uri());
        RestTemplate restTemplate = new RestTemplate();
        // reverse proxy mapping
        String[] pathSplit = request.uri().split("/");
        String rproxyRoute = pathSplit[1];
        String httpServiceProvider = bind.get(rproxyRoute);
        if(httpServiceProvider == null) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    request.protocolVersion(),
                    HttpResponseStatus.NOT_FOUND,
                    Unpooled.copiedBuffer("Not Found", CharsetUtil.UTF_8)
            );
            logger.warn("Invalid route: "+rproxyRoute);
            ctx.writeAndFlush(response);
        } else {
            int routeStringSize = rproxyRoute.length()+1;
            String uri = httpServiceProvider + request.uri().substring(routeStringSize);
            ResponseEntity<String> restResponse = null;
            switch (request.method().name()) {
                case "GET":
                    restResponse = restTemplate.getForEntity(uri, String.class);
                    break;
                case "POST":
                    // todo 发送post请求
                    byte[] bytes = new byte[request.content().readableBytes()];
                    request.content().readBytes(bytes);
                    MultiValueMap<String, String> httpHeaders = HttpHeaders2MultiValueMap.convert(request.headers());
                    HttpEntity<byte[]> requestEntity = new HttpEntity<>(bytes, httpHeaders);
                    restResponse = restTemplate.postForEntity(uri,requestEntity,String.class);
                    break;
            }
            logger.info("HTTP response received from server < status=" + Objects.requireNonNull(restResponse).getStatusCode().value() + " body=" + restResponse.getBody());
            FullHttpResponse response = new DefaultFullHttpResponse(
                    request.protocolVersion(),
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(
                            Objects.requireNonNull(restResponse.getBody()),
                            CharsetUtil.UTF_8
                    )
            );
            restResponse.getHeaders().forEach((k, v) -> {
                response.headers().set(k, v);
            });
            response.headers().set("Access-Control-Allow-Origin", "*");
            response.headers().set("Access-Control-Expose-Headers", "*");
            response.headers().set("Access-Control-Allow-Credentials", "true");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    logger.info("time_used=" + (System.currentTimeMillis() - timestampWhenStart) / 1000.0 + "s");
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.err(cause);
        ctx.close().addListener(future -> logger.info("Channel " + ctx.channel().remoteAddress() + " closed on exception"));
    }
}
