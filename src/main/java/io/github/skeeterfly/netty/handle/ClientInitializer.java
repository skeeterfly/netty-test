package io.github.skeeterfly.netty.handle;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.CountDownLatch;
/**
 * @Author: Skeeter
 * @Email:jeevan0612@qq.com
 * @Date: 2018-10-29
 * @Description:
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private CountDownLatch lathc;
    public ClientInitializer(CountDownLatch lathc) {
        this.lathc = lathc;
    }
    private ClientHandler handler;
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        handler =  new ClientHandler(lathc);
        sc.pipeline().addLast(new StringDecoder());//进行字符串的编解码设置
        sc.pipeline().addLast(new StringEncoder());
        sc.pipeline().addLast(new ReadTimeoutHandler(60));//设置超时时间
        sc.pipeline().addLast(handler);
    }
    public String getServerResult(){
        return handler.getResult();
    }
    public void resetLathc(CountDownLatch lathc) {
        handler.resetLatch(lathc);
    }

}