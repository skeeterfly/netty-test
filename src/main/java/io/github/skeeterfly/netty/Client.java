package io.github.skeeterfly.netty;

import io.github.skeeterfly.netty.handle.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

/**
 * @Author: Skeeter
 * @Email:jeevan0612@qq.com
 * @Date: 2018-10-29
 * @Description:
 */
public class Client {

    private static class SingletonHolder {
        static final Client instance = new Client();
    }
    public static Client getInstance(){
        return SingletonHolder.instance;
    }
    private EventLoopGroup group;
    private Bootstrap b;
    private ChannelFuture cf ;
    private ClientInitializer clientInitializer;
    private CountDownLatch lathc;
    private Client(){
        lathc = new CountDownLatch(0);
        clientInitializer = new ClientInitializer(lathc);
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(clientInitializer);
    }
    public void connect(){
        //192.168.43.51测试端口8766 192.168.43.102 线上端口8765
        try {
            this.cf = b.connect("127.0.0.1", 9000).sync();
            System.out.println("远程服务器已经连接, 可以进行数据交换..");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }
    public ChannelFuture getChannelFuture(){
        if(this.cf == null) {
            this.connect();
        }
        if(!this.cf.channel().isActive()){
            this.connect();
        }
        return this.cf;
    }
    public void close(){
        try {
            this.cf.channel().closeFuture().sync();
            this.group.shutdownGracefully();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String setMessage(String msg) throws InterruptedException{
        ChannelFuture cf =getInstance().getChannelFuture();//单例模式获取ChannelFuture对象
        cf.channel().writeAndFlush(msg);
        //发送数据控制门闩加一
        lathc = new CountDownLatch(1);
        clientInitializer.resetLathc(lathc);
        lathc.await();//开始等待结果返回后执行下面的代码
        return clientInitializer.getServerResult();
    }
    public static void main(String[] args) throws Exception {
        int i = 0;
        while (i<100) {
            sleep(1000);
            String test = Client.getInstance().setMessage("当前访问客户："+i);
            System.out.println(new Date(Long.parseLong(test)));//测试等待数据返回
            i++;
        }
    }
}