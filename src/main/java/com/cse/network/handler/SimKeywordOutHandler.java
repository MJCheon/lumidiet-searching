package com.cse.network.handler;

import com.cse.network.data.OutboundData;
import com.cse.network.data.SynKeywordResData;
import com.cse.network.response.ErrorResponse;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
public class SimKeywordOutHandler extends ChannelOutboundHandlerAdapter{
    //다음 Handler 로 넘길 결과
    private Object result;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);

        //에러인 경우
        if(msg instanceof ErrorResponse)
            result = msg;
        else{
            //유사 키워드 검색 기능인 경우
            if(msg instanceof SynKeywordResData){
                // 유사 키워드 리스트 및 문서 리스트 보내줌
                OutboundData synData = ((SynKeywordResData) msg).getSynData();
                String resultStr = new Gson().toJson(synData);
                ByteBuf buf = Unpooled.compositeBuffer().writeBytes(resultStr.getBytes());
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
                response.headers().set(HttpHeaders.CONTENT_LENGTH,response.content().readableBytes());
                response.headers().set(HttpHeaders.CONTENT_TYPE,"application/json; charset=utf-8");
                result = response;
            }
            //다른 기능일 경우에는 다른 처리 없이 result에 msg를 넣는다.
            else
                result = msg;

        }
        flush(ctx);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
        ctx.write(result);
    }
}
