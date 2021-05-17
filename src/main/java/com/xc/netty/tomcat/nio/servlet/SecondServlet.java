package com.xc.netty.tomcat.nio.servlet;

import com.xc.netty.tomcat.nio.http.XCRequest;
import com.xc.netty.tomcat.nio.http.XCResponse;
import com.xc.netty.tomcat.nio.http.XCServlet;

/**
 * description
 *
 * @author lichao chao.li07@hand-china.com 5/17/21 10:30 AM
 */
public class SecondServlet extends XCServlet {

    @Override
    public void doGet(XCRequest request, XCResponse response) throws Exception {
        doPost(request,response);
    }

    @Override
    public void doPost(XCRequest request, XCResponse response) throws Exception {
        System.out.println("This is second servlet from NIO.");
        response.write("This is second servlet from NIO.");
    }
}
