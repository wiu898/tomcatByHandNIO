package com.xc.netty.tomcat.nio.http;

/**
 * Servlet
 *
 * @author lichao chao.li07@hand-china.com 5/17/21 10:30 AM
 */
public abstract class XCServlet {

    public void service(XCRequest request, XCResponse response) throws Exception{
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else{
            doPost(request,response);
        }
    }

    public abstract void doGet(XCRequest request, XCResponse response) throws Exception;

    public abstract void doPost(XCRequest request, XCResponse response) throws Exception;
}
