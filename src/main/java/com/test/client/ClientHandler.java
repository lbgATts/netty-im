package com.test.client;

import com.test.protocol.PacketCodec;
import com.test.protocol.packet.LoginRequestPacket;
import com.test.protocol.packet.LoginResponsePacket;
import com.test.protocol.packet.MessageResponsePacket;
import com.test.protocol.packet.Packet;
import com.test.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.UUID;

/**
 * @Author: shanying
 * @Date: 2019-08-01 16:50
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {


        private PacketCodec packetCodec = PacketCodec.INSTANCE;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(new Date() + " 客户端开始登录");

            LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
            loginRequestPacket.setUserId(UUID.randomUUID().toString());
            loginRequestPacket.setUsername("flash");
            loginRequestPacket.setPassword("password");

            ByteBuf encodedBuf = ByteBufAllocator.DEFAULT.ioBuffer();
            encodedBuf = packetCodec.encode(encodedBuf, loginRequestPacket);
            ctx.writeAndFlush(encodedBuf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf byteBuf = (ByteBuf) msg;

            Packet packet = new PacketCodec().decode(byteBuf);

            if (packet instanceof LoginResponsePacket) {
                LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;

                if (loginResponsePacket.isSuccess()) {
                    System.out.println(new Date() + ": 客户端登录成功");
                    //设置属于这个Channel的属性
                    LoginUtil.markAsLogin(ctx.channel());
                } else {
                    System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
                }
            } else if (packet instanceof MessageResponsePacket) {
                MessageResponsePacket messageResponsePacket = (MessageResponsePacket) packet;
                System.out.println(new Date() + ": 收到服务端的消息: " + messageResponsePacket.getMessage());
            }
        }
    }
