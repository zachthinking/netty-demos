package com.github.zachthinking.netty.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 自动停止的服务端
 */
public class SelfStopServer {

    static int port = 12345;
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    ServerBootstrap b = new ServerBootstrap();
    Channel channel;

    void start() throws InterruptedException {
        try {
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            System.out.println("bind on port: " + port);

            channel = f.channel();
            System.out.println("channel: " + channel.toString());
            channel.closeFuture().sync();
        } finally {
            System.out.println("stop ...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void stop() {
        channel.close();
    }

    public static void main(String[] args) throws InterruptedException {
        SelfStopServer server = new SelfStopServer();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            server.stop();
        }).start();
        server.start();
    }
}
