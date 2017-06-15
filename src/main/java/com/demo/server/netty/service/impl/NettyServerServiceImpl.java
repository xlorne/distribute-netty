package com.demo.server.netty.service.impl;


import com.demo.server.netty.handler.SocketHandler;
import com.demo.server.netty.service.NettyServerService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by yuliang on 2017/4/12.
 */
@Service
public class NettyServerServiceImpl implements NettyServerService {


    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture f;
    private ServerBootstrap b;



    private int port = 8090;



    @Override
    public synchronized void start() {
        bossGroup = new NioEventLoopGroup(); // (1)
        workerGroup = new NioEventLoopGroup();
        try {
            b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ByteArrayDecoder());
                            pipeline.addLast(new SocketHandler());
                            pipeline.addLast(new ByteArrayEncoder());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            f = b.bind(port).sync();

            System.out.println("socket starting....");
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if (f != null)
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        if (workerGroup != null)
            workerGroup.shutdownGracefully();
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
        System.out.println("socket closing....");
    }

}

