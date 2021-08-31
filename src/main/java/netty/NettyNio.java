package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import org.ietf.jgss.Oid;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyNio {

    public static void main(String[] args) throws Exception {
        server(8801);
    }
    public static void server(int port) throws Exception{
        final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi\r\n", Charset.forName("UTF-8"))
        );

        EventLoopGroup group = new NioEventLoopGroup();
        try{
            //创建一个 ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();

            //使用 NioEventLoopGroup 允许非阻塞模式（NIO）
            b.group(new NioEventLoopGroup(),new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    //.指定 ChannelInitializer 将给每个接受的连接调用
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //.写信息到客户端，并添加 ChannelFutureListener 当一旦消息写入就关闭连接
                                    ctx.writeAndFlush(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            //绑定服务器来接受连接
            ChannelFuture f = b.bind().sync();
            //释放所有资源
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
