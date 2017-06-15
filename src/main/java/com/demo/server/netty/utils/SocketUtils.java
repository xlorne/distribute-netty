package com.demo.server.netty.utils;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yuliang on 2016/3/7.
 */
public class SocketUtils {

    public static void send(final ChannelHandlerContext ctx, final byte[] data) {
        if (ctx == null)
            return;
        final ByteBuf byteBufMsg = ctx.alloc().buffer(data.length);
        byteBufMsg.writeBytes(data);
        ctx.writeAndFlush(byteBufMsg);
    }

}
