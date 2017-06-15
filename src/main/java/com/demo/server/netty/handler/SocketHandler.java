package com.demo.server.netty.handler;


import com.demo.server.netty.utils.SocketUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * Created by yuliang on 2017/4/12.
 */
public class SocketHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //todo 根据自己的负载情况获取相应的socket服务器信息
        String ip = "192.168.1.188:2030";

        SocketUtils.send(ctx,ip.getBytes());
        ctx.close();
    }
}
