package com.cse.network.handler;

import com.cse.module.DocSearchModule;
import com.cse.network.data.OutboundData;
import com.cse.network.data.SimKeywordMethod;
import com.cse.network.data.SynKeywordResData;
import com.cse.network.interfaces.IFindKeyword;
import com.cse.network.response.BadRequestResponse;
import com.cse.spark.Word2VecInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by bullet on 16. 10. 25.
 */
public class SimKeywordInHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

        //현재 메소드와 일치하는 경우
        if(o instanceof SimKeywordMethod){
            SimKeywordMethod method = (SimKeywordMethod)o;
            //키워드가 null인 경우
            if(method.getSearchKeyword()==null){
                channelHandlerContext.pipeline().write(new BadRequestResponse());
                return;
            }
            //Word2Vec을 이용하여 유사 주제어 찾기 기능을 수행한다.
            Word2VecInstance.getInstance().getSymKeyword(method.getSearchKeyword(), new IFindKeyword() {
                @Override
                public void findSynData(OutboundData synData) {
                    channelHandlerContext.pipeline().write(new SynKeywordResData(synData));
                }

                @Override
                public void error() {
                    channelHandlerContext.pipeline().write(new BadRequestResponse());
                }
            });
        }
        //다른 메소드인 경우
        else
            channelHandlerContext.fireChannelRead(o);

    }
}
