package com.wohl.worox.handler;

import com.wohl.worox.conf.entity.HttpFilterConf;
import com.wohl.worox.conf.reader.HttpFilterConfReader;
import com.wohl.worox.context.JentitiContext;
import com.wohl.worox.util.log.Logger;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FilterHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = new Logger(this);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpFilterConf httpFilterConf  = ((HttpFilterConfReader) JentitiContext.get(HttpFilterConfReader.class)).getConf();
        Deque<String> methods = httpFilterConf.getMethods();
        Map<String, List<String>> headers = httpFilterConf.getHeaders();
        FullHttpRequest request = (FullHttpRequest) msg;
        boolean goodRequest = true;
        if(!methods.contains(request.method().name()))
            goodRequest = false;
        else {
            for (String s : headers.keySet()) {
                if (!request.headers().contains(s)) {
                    goodRequest = false;
                    break;
                } else {
                    AtomicBoolean asLeastFindOne = new AtomicBoolean(false);
                    headers.forEach((k,v) -> {
                        List<String> existingValues = request.headers().getAll(k);
                        v.forEach((var0) -> {
                            existingValues.forEach((var1) -> {
                                if(var0.equals(var1))
                                    asLeastFindOne.set(true);
                            });
                        });
                    });
                    if(!asLeastFindOne.get())
                        goodRequest = false;
                }
            }
        }
        if(goodRequest) {
            ctx.fireChannelRead(msg);
        } else {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    request.protocolVersion(),
                    HttpResponseStatus.FORBIDDEN,
                    Unpooled.copiedBuffer("Filtered", CharsetUtil.UTF_8)
            );
            logger.warn("Filtered HTTP request from client > version="+ request.protocolVersion() +" method="+request.method()+" path="+request.uri());
            ctx.writeAndFlush(response);
        }
    }
}
