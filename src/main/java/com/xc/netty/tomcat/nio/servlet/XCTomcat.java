package com.xc.netty.tomcat.nio.servlet;

import io.netty.handler.codec.http.HttpRequest;
import com.xc.netty.tomcat.nio.http.XCRequest;
import com.xc.netty.tomcat.nio.http.XCResponse;
import com.xc.netty.tomcat.nio.http.XCServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Tomcat启动类
 *
 * @author lichao chao.li07@hand-china.com 5/17/21 10:30 AM
 */
public class XCTomcat {

    private int port = 8080;   //默认端口

    private ServerSocket server;

    private Properties webxml = new Properties();

    private Map<String, XCServlet> servletMapping = new HashMap<String, XCServlet>();

    public static void main(String[] args) {
        new XCTomcat().start();
    }

    /*
     * tomcat的启动入口
     */
    private void start() {
        //1.加载web.properties文件解析配置进行缓存
        init();
        //boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //创建netty服务端对象
        ServerBootstrap server = new ServerBootstrap();

        //配置服务参数
        try{
            server.group(bossGroup,workerGroup)
                    //配置主线程的处理逻辑
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        //处理回调逻辑 - netty采用链式编程 责任链模式
                        @Override
                        protected void initChannel(Channel client) throws Exception {
                            //addLast有顺序要求 addLast为向后添加操作
                            //处理相应结果 用http格式输出
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //处理用户请求，用户请求需要解码
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //用户自己的业务逻辑
                            client.pipeline().addLast(new XCTomcatHandler());
                        }
                    })
                    //配置主线程分配的最大线程数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //配置工作线程保持长链接
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //启动服务
            ChannelFuture future = server.bind(this.port).sync();
            System.out.println("Tomcat 已启动,监听端口是:" + this.port);
            //关闭feature
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭主线程和worker线程
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void init() {
        //读取配置文件
        String WEB_INF = this.getClass().getResource("/").getPath();
        try{
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
            webxml.load(fis);
            for(Object k : webxml.keySet()){
                String key = k.toString();
                if(key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$","");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    //反射创建Servlet容器
                    XCServlet obj = (XCServlet)Class.forName(className).newInstance();
                    //将url和servlet建立映射关系
                    servletMapping.put(url,obj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * 用户客制化的逻辑
     */
    private class XCTomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest)msg;
                XCRequest request = new XCRequest(ctx,req);
                XCResponse response = new XCResponse(ctx,req);

                String url = request.getUrl();
                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request,response);
                }else{
                    response.write("404 - Not Found!");
                }
            }
        }
    }
}
