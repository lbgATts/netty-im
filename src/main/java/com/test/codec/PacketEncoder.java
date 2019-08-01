package com.test.codec;

import com.test.protocol.PacketCodec;
import com.test.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: shanying
 * @Date: 2019-08-01 17:58
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        PacketCodec.INSTANCE.encode(out, msg); //需要将out往下传，encode需要这个编解码
    }
}
