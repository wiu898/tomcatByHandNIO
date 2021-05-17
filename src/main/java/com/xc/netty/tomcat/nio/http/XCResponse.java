package com.xc.netty.tomcat.nio.http;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Response
 *
 * @author lichao chao.li07@hand-china.com 5/17/21 10:30 AM
 */
public class XCResponse {

    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public XCResponse(ChannelHandlerContext ctx, HttpRequest req) {
    }

    /*
     * Response输出-通常是返回html或者json字符串
     */
    public void write(String s) throws Exception{
        if(s == null || s.length() == 0){
            return;
        }
        try{
            FullHttpResponse response = new DefaultFullHttpResponse(
                    //设置HTTP版本号为1.1
                    HttpVersion.HTTP_1_1,
                    //设置返回的HTTP状态码
                    HttpResponseStatus.OK,
                    //统一输出格式为UTF-8
                    Unpooled.wrappedBuffer(s.getBytes("UTF-8"))
            );
            response.headers().set("Content-Type","text/html");
            ctx.write(response);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ctx.flush();
            ctx.close();
        }

    }
}
