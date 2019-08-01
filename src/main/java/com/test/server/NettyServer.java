package com.test.server;

import com.test.protocol.packet.*;
import com.test.protocol.PacketCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Author: shanying
 * Date: 2019-08-01 10:34
 */
public class NettyServer {

    private static final int port = 8080;

    public static void main(String[] args) {
        //默认线程数是 Math.max(1, 2 * processor)
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootStrap = new ServerBootstrap();
        serverBootStrap
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childOption(ChannelOption.TCP_NODELAY, true) //立即发送包
                //todo childHandler 是服务端处理请求用的handler，是入站事件
                .childHandler(new ChannelInitializer<NioSocketChannel>() { //channelHandler
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });
            bind(serverBootStrap, port);
    }

    static void bind(final ServerBootstrap serverBootstrap, int port) {
        //bind 是异步方式，返回的是 ChannelFuture
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("端口[" + port + "]绑定成功");
            } else {
                System.out.println("端口[" + port + "]绑定失败");
            }
        });
    }

    static class FirstServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("跟服务端的连接建立");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            System.out.println(new Date() + ": 服务端读到数据" +  buf.toString(Charset.forName("UTF-8")));
            System.out.println(new Date() + ": 服务端写出数据");
            ByteBuf out = getByteBuf(ctx);
            ctx.channel().writeAndFlush(out);
        }

        private ByteBuf getByteBuf(ChannelHandlerContext context) {
            byte[] bytes = "欢迎来到Netty世界".getBytes(Charset.forName("UTF-8"));
            ByteBuf buf = context.alloc().buffer(); //todo AbstractByteBufAllocator 根据平台是否支持UnSafe来确实是directBuf还是heapBuf
            buf.writeBytes(bytes);
            return buf;
        }
    }
}
