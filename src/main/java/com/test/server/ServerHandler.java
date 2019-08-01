package com.test.server;

import com.test.protocol.PacketCodec;
import com.test.protocol.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @Author: shanying
 * @Date: 2019-08-01 16:51
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{

    private PacketCodec packetCodec = PacketCodec.INSTANCE;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(new Date() + " 客户端已经登录到服务端");

        ByteBuf buf = (ByteBuf) msg;
        PacketCodec codec = new PacketCodec();
        Packet packet = codec.decode(buf);
        ByteBuf encodedBuf = ByteBufAllocator.DEFAULT.ioBuffer();

        if (packet instanceof LoginRequestPacket) {
            LoginRequestPacket loginRequestPacket  =  (LoginRequestPacket) packet;
            String username = loginRequestPacket.getUsername();
            String password = loginRequestPacket.getPassword();
            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            if (validLogin(username, password)) {
                loginResponsePacket.setSuccess(true);
            } else {
                loginResponsePacket.setReason("登录失败");
            }
            encodedBuf = packetCodec.encode(encodedBuf, loginResponsePacket);
            ctx.writeAndFlush(encodedBuf);
        } else if (packet instanceof MessageRequestPacket) {
            MessageRequestPacket messageRequestPacket = ((MessageRequestPacket) packet);
            System.out.println("服务端收到来自客户端的消息:" + messageRequestPacket.getMessage());

            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");

            ByteBuf messageBuf = packetCodec.encode(encodedBuf, messageResponsePacket);
            ctx.writeAndFlush(messageBuf);
        }
    }

    private boolean validLogin(String userName, String password) {
        return true;
    }
}
