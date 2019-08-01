package com.test.client;

import com.test.client.handler.LoginResponseHandler;
import com.test.client.handler.MessageResponseHandler;
import com.test.codec.PacketDecoder;
import com.test.codec.PacketEncoder;
import com.test.protocol.PacketCodec;
import com.test.protocol.packet.*;
import com.test.util.LoginUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Author: shanying
 * Date: 2019-08-01 10:34
 */
public class NettyClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    private static final int RETRY_TIMES = 3;
    public static void main(String[] args) {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new ClientHandler());
                        ch.pipeline().addLast(new PacketDecoder()); //入站事件
                        ch.pipeline().addLast(new LoginResponseHandler()); //入站事件
                        ch.pipeline().addLast(new MessageResponseHandler()); //入站事件
                        ch.pipeline().addLast(new PacketEncoder()); //出站事件
                    }
                });

        connect(bootstrap, HOST, PORT, RETRY_TIMES);

    }

    //todo  test point
    static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host,port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("客户端连接成功");
                //连接成功后开启线程一直发消息
                Channel channel = ((ChannelFuture) future).channel();
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.out.println("客户端连接失败");
            } else {
                //还有重试次数，重试连接
                int delay = new Random().nextInt(10);
                bootstrap.config().group().schedule(()->connect(bootstrap, host,port, retry-1),delay, TimeUnit.SECONDS);
            }
        });
    }

    static void startConsoleThread(Channel channel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    if (LoginUtil.hasLogin(channel)) {
                        System.out.println("输入消息发送至服务端: ");
                        Scanner scanner = new Scanner(System.in);
                        String line = scanner.nextLine();
                        MessageRequestPacket messageRequestPacket = new MessageRequestPacket();
                        messageRequestPacket.setMessage(line);
//                        ByteBuf buf = new PacketCodec().encode(messageRequestPacket);
                        channel.writeAndFlush(messageRequestPacket); //todo 从这里出去的消息先经过 PacketEncode
                    }
                }
            }
        }).start();
    }

    static class FirstClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            System.out.println(new Date() + ": 客户端写出数据");

            // 1.获取数据
            ByteBuf buffer = getByteBuf(ctx);

            // 2.写数据
            ctx.channel().writeAndFlush(buffer);
        }

        private ByteBuf getByteBuf(ChannelHandlerContext ctx) {

            byte[] bytes = "你好，这是来自客户端的问候：".getBytes(Charset.forName("utf-8"));

            ByteBuf buffer = ctx.alloc().buffer();

            buffer.writeBytes(bytes);

            return buffer;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
        }
    }

}
