package com.test.server.handler;

import com.test.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: shanying
 * Date: 2019-08-01 18:23
 */
public class LoginRequestHandler extends SimpleChannelInboundHandler<Packet> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {

    }
}
