package com.demo.server.netty.service.impl;


import com.demo.server.netty.service.InitService;
import com.demo.server.netty.service.NettyServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yuliang on 2017/4/12.
 */
@Service
public class InitServiceImpl implements InitService {


    @Autowired
    private NettyServerService nettyServerService;

    @Override
    public void start() {
        nettyServerService.start();
    }

    @Override
    public void close() {
        nettyServerService.close();
    }
}
