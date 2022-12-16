package com.github.zachthinking.netty.first;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 第一个Netty程序之客户端
 *
 * 连接服务端，发送字符串，然后断开连接
 *
 */
public final class FirstClient {

    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap b = new Bootstrap();
    Channel channel;

    void setup() {
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new LoggingHandler(LogLevel.INFO));
                        p.addLast(new StringEncoder());
                    }
                });
    }

    void connect(String host, int port) throws InterruptedException {
        ChannelFuture f = b.connect(host, port).sync();
        channel = f.channel();
        System.out.println("channel: " + channel.toString());
    }

    void send(String msg) {
        channel.writeAndFlush(msg);
    }

    void close() {
        channel.close();
    }

    void destory() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 12345;
        FirstClient client = new FirstClient();
        client.setup();
        client.connect(host, port);
        client.send("hello");
        client.close();
        client.destory();
    }
}