# 基于netty框架的socket长连接负载均衡解决方案

## 前言
物联网如今是一个大的趋势，但是概念还比较新颖。大家对这一块的技术积累也比较匮乏，借此前段时间摩拜单车出现了大规模瘫痪的现象。我们今天来讨论一下物联网项目的开发方式。

## Socket Delivery Server 
SDS是基于此原理封装的高可用框架，源码请参考：![SDS](https://github.com/1991wangliang/sds)

## 关于tcp/ip 相关的知识点

tcp三次握手，四次挥手   
[http://blog.csdn.net/whuslei/article/details/6667471](http://blog.csdn.net/whuslei/article/details/6667471)    
tcp Client/server 最大连接数     
[http://blog.csdn.net/huangjin0507/article/details/52399957](http://blog.csdn.net/huangjin0507/article/details/52399957)   
close_wait问题处理方案   
[http://www.cnblogs.com/sunxucool/p/3449068.html](http://www.cnblogs.com/sunxucool/p/3449068.html)   

## socket通讯的单机瓶颈

物联网的项目socket使用方式有两种：

1. 短连接的socket请求
2. 维持socket长连接的请求

对于socket短链接来说就好比是http请求，请求服务器，服务器返回数据以后请求管道就关闭了，服务器与客户端的链接就释放了。但是对于socket长链接就不同了，当设备与服务器建立连接以后就要一直保持连接，或者说保持较长时间的链接，那么就会大量消耗服务器的资源。若存在大量的这样的请求以后服务器终究会受不了垮掉。通过对TcpClient/server最大连接数我们得知单机socket服务是存在最大链接数限制。尽管理论值很大，但还要考虑到实际服务器的内存／cpu／带宽等条件，我们不可能指望单机承载特别大的链接请求。

## 该如何负载均衡socket长连接的请求

提到负载均衡大家可能会想到很多负载均衡的框架，比如说比较出名的：nginx。但是悲催的是他是基于转发的方式，不能应用在socket长链接请求上。在这一块目前还没有特别优秀的处理框架，而且从技术角度来分析也不可能存在。那我们只能自己想办法了。


## socket分发服务架构图

![ ](/readme/WX20170615-172308@2x.png)

1、	设备请求分发服务器，分发服务器返回有效的socket服务器ip与port，然后断开连接。  
    a)	设备与服务器建立连接。  
    b)	服务器接收到连接请求后，立即将分配好的socket服务器ip与port信息响应给设备。  
    c)	服务器主动断开socket连接。  
2、	设备得到ip与port以后，设备去连接socket服务器，然后与其进行协议通讯。  
    a)	设备连接到socket服务器。  
    b)	socket服务器响应连接成功响应信息。  
    c)  设备与socket服务器保持长链接通讯。
    
*.	若设备未收到连接成功响应则再次尝试连接，若三次请求依旧没有成功建立连接，那么设备需要去请求分发服务器然后再重新上述操作。   
*.  当设备在异常情况下链接不上socket服务器时，依旧尝试三次若不能成功，则直接请求分发服务器，然后再重复上述操作。


## 分发服务器处理业务

我们来看一下分发服务器该处理的业务：

```$xslt

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //todo 根据自己的负载情况获取相应的socket服务器信息
        String ip = "192.168.1.188:2030";

        SocketUtils.send(ctx,ip.getBytes());
        ctx.close();
    }
    
```

*. ip的获取需要根据自己的业务来完成。

## 总结  
我们通过这样的方式就可以轻松的解决大量设备与服务器通讯的问题，若后面有更多的设备请求只需添加更多的socket服务器即可。当然可能大家担心分发服务器受不了，其实这是多余的，因为分发服务器只做转发，而且完成处理以后就直接把链接给释放，并且当设备拿到socket服务器的ip地址以后就将不在访问分发服务器了，它的压力是可控的不会特别也不会频繁。


## 关于distribute-netty：
该demo仅仅用于说明分发服务器的工作原理，具体的业务实现还需要根据自己的业务来完成。

技术交流群：554855843

