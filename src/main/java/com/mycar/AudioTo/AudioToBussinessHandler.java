package com.mycar.AudioTo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioToBussinessHandler extends ChannelInboundHandlerAdapter {
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
//        group.writeAndFlush("[服务器] - " + currchannel.remoteAddress() + " 离开\n");
        super.handlerRemoved(ctx);
    }
}
