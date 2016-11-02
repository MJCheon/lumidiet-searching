package com.cse.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Word2VecServer {
    private EventLoopGroup eventLoopGroup;
    private ServerBootstrap serverBootStrap;

    public Word2VecServer(){
        eventLoopGroup = new NioEventLoopGroup();
        serverBootStrap = new ServerBootstrap();
        serverBootStrap.group(eventLoopGroup)
                .localAddress(new InetSocketAddress(9999))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ApiChannelInitializer());
    }

    public void startServer() throws Exception{
        try {
            ChannelFuture serverChannelFuture = serverBootStrap.bind().sync();
            serverChannelFuture.channel().closeFuture().sync();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            eventLoopGroup.shutdownGracefully().sync();
        }
    }
}
