package io.github.skeeterfly.netty;

import io.github.skeeterfly.netty.handle.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author: Skeeter
 * @Email:jeevan0612@qq.com
 * @Date: 2018-10-29
 * @Description:
 */
public class Server {


    public void bind(int port) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            /*最大连接数量设定 BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。*/
            b.group(bossGroup,workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,1024).childHandler(new ChildChannelHandler());
            //绑定端口，等待同步成功
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) {
        int port = 9000;
        try {
            new Server().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
