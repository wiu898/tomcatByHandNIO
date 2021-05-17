package com.xc.netty.tomcat.nio.http;

import java.util.List;
import java.util.Map;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;


/**
 * Request
 *
 * @author lichao chao.li07@hand-china.com 5/17/21 10:30 AM
 */
public class XCRequest {

    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public XCRequest(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    /*
     * 解析request中的参数
     */
    public Map<String, List<String>> getParameters(){
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        return decoder.parameters();
    }

    public String getParameter(String name){
        Map<String, List<String>> paramMap = getParameters();
        List<String> params = paramMap.get(name);
        if(null == params){
            return null;
        }else {
            return params.get(0);
        }
    }

    public String getUrl(){
        return this.req.uri();
    }

    public String getMethod(){
        return this.req.method().name();
    }

}
