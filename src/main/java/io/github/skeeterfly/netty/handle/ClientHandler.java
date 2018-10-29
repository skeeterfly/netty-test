package io.github.skeeterfly.netty.handle;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


import java.util.concurrent.CountDownLatch;

/**
 * @Author: Skeeter
 * @Email:jeevan0612@qq.com
 * @Date: 2018-10-29
 * @Description:
 */
public class ClientHandler extends ChannelHandlerAdapter {

    private CountDownLatch lathc;
    private String result;
    public ClientHandler(CountDownLatch lathc) {
        this.lathc = lathc;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        result = (String) msg;
        lathc.countDown();// 消息接收后释放同步锁，lathc是从Client加一传回来的
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
    public void resetLatch(CountDownLatch lathc) {
        this.lathc = lathc;
    }

    public String getResult() {
        return result;
    }
}
