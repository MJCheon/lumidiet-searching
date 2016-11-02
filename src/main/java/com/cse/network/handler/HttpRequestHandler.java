package com.cse.network.handler;

import com.cse.network.data.BaseMethod;
import com.cse.network.data.SimKeywordMethod;
import com.cse.network.response.NoSuchMethodResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bullet on 16. 10. 25.
 */
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<Object>{
        private static final String METHOD_KEYWORDLIST = "keywordlist";
        private String method;
        private Properties queryParameters;
        private BaseMethod requestMethod;
        private boolean isError = false;

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            ctx.channel().close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
            //HTTP HEADER인 경우
            if (obj instanceof HttpRequest) {

                //Header 내에서 method(Segment)와 Query Parameter를 처리한다.
                HttpRequest httpRequest = (HttpRequest) obj;
                method = getMethod(httpRequest.uri());
                queryParameters = getParameter(httpRequest.uri());

                //method에 따라 다른 처리를 진행한다.
                switch (method) {
                    //유사 Keyword method인 경우
                    case METHOD_KEYWORDLIST:
                        processFindSymKeyword(channelHandlerContext);
                        break;
                    //존재하지 않는 method를 요청했을 경우
                    default:
                        //바로 에러 핸들러로 리턴시킨다.
                        isError = true;
                        channelHandlerContext.pipeline().write(new NoSuchMethodResponse());
                        break;
                }
            } else {
                //채널이 닫힌 경우 더이상의 처리를 진행하지 않는다.
                if (isError)
                    return;
            /*
            HttpContent httpContent = (HttpContent) obj;
            content += httpContent.content().toString(CharsetUtil.UTF_8);
            if (obj instanceof DefaultLastHttpContent){

            }
            */
            }
        }

        /**
         * SimKeywordInHandler의 read method를 호출한다.
         *
         * @param ctx ChannelHandlerContext
         */
        private void processFindSymKeyword(ChannelHandlerContext ctx) {

            //method 요청 객체를 생성하여 다음 Handler에게 Read 를 Trig한다.
            requestMethod = new SimKeywordMethod();
            ((SimKeywordMethod) requestMethod).setSearchKeyword((String) queryParameters.get("keyword"));
            ctx.fireChannelRead(requestMethod);
        }

        /**
         * Uri 내의 Parameter List를 반환한다.
         *
         * @param uri Request Uri
         * @return Query Parameter Properties
         */
        private Properties getParameter(String uri) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            Properties properties = new Properties();
            for (Map.Entry<String, List<String>> stringListEntry : queryStringDecoder.parameters().entrySet()) {
                String key = stringListEntry.getKey();
                String value = "";
                if (stringListEntry.getValue().size() != 0)
                    value = stringListEntry.getValue().get(0);
                properties.put(key, value);
            }
            return properties;

        }

        /**
         * Http의 첫번째 Uri Segment를 반환한다.
         *
         * @param uri 전체 Uri Segment
         * @return 첫번째 Uri Segment
         */
        private String getMethod(String uri) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            String path = queryStringDecoder.path();
            path = path.substring(1);
            String[] splitedUri = path.split("/");
            return splitedUri[0];
        }

        @Override
        public boolean isSharable() {
            return super.isSharable();
        }
}
