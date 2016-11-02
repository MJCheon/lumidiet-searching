package com.cse.network;

import com.cse.network.handler.ErrorOutHandler;
import com.cse.network.handler.HttpRequestHandler;
import com.cse.network.handler.SimKeywordInHandler;
import com.cse.network.handler.SimKeywordOutHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by bullet on 16. 10. 25.
 */
public class ApiChannelInitializer extends ChannelInitializer<SocketChannel> {
    public ApiChannelInitializer(){

    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("HTTPDECODER",new HttpRequestDecoder());
        socketChannel.pipeline().addLast("REQUEST_HANDLER",new HttpRequestHandler());
        socketChannel.pipeline().addLast("SIMKEYWORD_REQUEST_HANDLER",new SimKeywordInHandler());
        socketChannel.pipeline().addLast("HTTPENCODER",new HttpResponseEncoder());
        socketChannel.pipeline().addLast("SIMKEYWORD_RESPONSE_HANDLER",new SimKeywordOutHandler());
        socketChannel.pipeline().addLast("ERROR_RESPONSE_HANDLER",new ErrorOutHandler());
    }
}
