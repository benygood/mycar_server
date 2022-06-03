package com.mycar.audioFrom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioFromBussinessHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger= LogManager.getLogger(ChannelInboundHandlerAdapter.class);

    public static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private Channel channel;
    /**
     * 发送的数据要与编码器匹配
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info(">>>>>>>>>>");
        logger.info("clientId="+ctx.channel().id().asShortText());
        group.add(ctx.channel());
        super.channelActive(ctx);
    }
    /**
     * ReferenceCountUtil.retain(msg);
     * ByteBuf可以利用直接内存避免拷贝数据到用户空间，并且Netty还使用池化技术降低内存使用率。
     * 因为用到了池化技术，Netty需要将用完的对象放回池中，java的垃圾回收器无法完成此功能，因此引入了引用计数，将用完的对象放回池中。
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        channel = ctx.channel();
        try {
            if(msg!=null){
                group.forEach(ch ->{
                    if(channel != ch){
                        ReferenceCountUtil.retain(msg);
                        ReferenceCountUtil.retain(msg);
                        ch.writeAndFlush(msg);
                        ReferenceCountUtil.release(msg);
                    }
                });
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel currchannel = ctx.channel();
        super.handlerRemoved(ctx);
    }
}
