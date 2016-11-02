package com.cse.network.handler;

import com.cse.network.response.BadRequestResponse;
import com.cse.network.response.NoSuchMethodResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Created by bullet on 16. 10. 25.
 */
public class ErrorOutHandler extends ChannelOutboundHandlerAdapter{
    private Object result;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        //해당 메서드가 존재하지 않는 경우
        if (msg instanceof NoSuchMethodResponse) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            response.headers().set("Content-Length", response.content().readableBytes());
            result = response;
        }
        //메서드는 맞지만 잘못된 요청인 경우
        else if (msg instanceof BadRequestResponse) {
//            ByteBuf buf = Unpooled.compositeBuffer().writeBytes("Error".getBytes());
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            response.headers().set("Content-Length", response.content().readableBytes());
            result = response;
        } else
            //다른 메시지일 경우 바로 다음 것으로 넘겨준다.
            result = msg;
        //결과를 flush 한다.
        flush(ctx);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
        if (ctx.channel().isActive())
            ctx.writeAndFlush(result).addListener(ChannelFutureListener.CLOSE);
        else
            ctx.channel().close().addListener(ChannelFutureListener.CLOSE);

        //result resource를 해제한다.
        ((FullHttpResponse) result).release();

    }
}
