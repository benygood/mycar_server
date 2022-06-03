package com.mycar.image;

import com.mycar.utils.BufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@ChannelHandler.Sharable
public class ImageChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LogManager.getLogger(ImageChannelInitializer.class);
    static final EventExecutorGroup group = new DefaultEventExecutorGroup(2);
    public ImageChannelInitializer() throws InterruptedException {
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        byte[] bytes = BufUtil.hexStringToBytes("FFD9FFD8FEFEFAFBFC");
        //stripDelimiter去掉分隔符
        //Unpooled.copiedBuffer将String转为ByteBuf对象
        pipeline.addLast(new DelimiterBasedFrameDecoder(30000*2,false, Unpooled.copiedBuffer(bytes)));
        pipeline.addLast(group, "ImageBussinessHandler", new ImageBussinessHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("exceptionCaught>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.info(cause);
        super.exceptionCaught(ctx, cause);
    }
}
