package com.mycar.audioFrom;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ChannelHandler.Sharable
public class AudioFromChannelInitializer  extends ChannelInitializer<SocketChannel>  {
    private static final Logger logger= LogManager.getLogger(AudioFromChannelInitializer.class);

    static final EventExecutorGroup group = new DefaultEventExecutorGroup(2);

    public AudioFromChannelInitializer() throws InterruptedException {
    }

    /**
     * server 与 client 编解码和数据格式必须一致
     * */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(group,"AudioFromBussinessHandler",new AudioFromBussinessHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("exceptionCaught>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.info(cause);
        super.exceptionCaught(ctx, cause);
    }
}
