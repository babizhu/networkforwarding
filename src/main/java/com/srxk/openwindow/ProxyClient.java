package com.srxk.openwindow;

import com.srxk.openwindow.handlers.MsgDecoder;
import com.srxk.openwindow.handlers.WindowsManagerHandler;
import com.srxk.openwindow.pojo.WindowStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.nio.channels.ConnectionPendingException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyClient {

  private final String host;
  private int port;
  private EventLoopGroup loop = new NioEventLoopGroup();
  //  private Channel channel;
  private WindowsManagerHandler handler;

  public ProxyClient(String host, int port) {
    this.host = host;
    this.port = port;
  }


  public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop) {
    if (bootstrap != null) {
      final WindowsManagerHandler windowsManagerHandler = new WindowsManagerHandler(this);
//      final MyInboundHandler handler = new MyInboundHandler(this);
      bootstrap.group(eventLoop);
      bootstrap.channel(NioSocketChannel.class);
      bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
      bootstrap.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
          socketChannel.pipeline().addLast(new MsgDecoder());
          socketChannel.pipeline().addLast(windowsManagerHandler);
        }
      });
//      bootstrap.remoteAddress("localhost", 8888);
//      System.out.println(port);
      bootstrap.connect(host, port).addListener(new ConnectionListener(this));
//      try {
////        Channel channel = bootstrap.connect(host,port).sync().channel();
////        Channel channel = bootstrap.connect(host,port).await().channel();
////        System.out.println( " bootstrap.connected");
////        System.out.println("init " + channel.hashCode());
//
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      System.out.println();
    }
    return bootstrap;
  }

  public void connect() {
    createBootstrap(new Bootstrap(), loop);

  }

  private void closeWindow(byte[] address) throws ConnectTimeoutException {

    System.out.println("ProxyClient.closeWindow");

    if (handler == null) {
      throw new ConnectionPendingException();
    }
    handler.closeWindow(address);

  }

  private void openWindow(byte[] address) throws ConnectTimeoutException {

    System.out.println("ProxyClient.closeWindow");

    if (handler == null) {
      throw new ConnectionPendingException();
    }
    handler.openWindow(address);

  }

  private void stopWindow(byte[] address) throws ConnectTimeoutException {

    System.out.println("ProxyClient.stopWindow");

    if (handler == null) {
      throw new ConnectionPendingException();
    }
    handler.stopWindow(address);

  }

  private WindowStatus queryWindow(byte[] address) throws ConnectTimeoutException {

//    System.out.println("ProxyClient.queryWindow");

    if (handler == null) {
      throw new ConnectionPendingException();
    }
    WindowStatus windowStatus = handler.queryWindow(address);

    return windowStatus;

  }

  public void connectSuccess(Channel channel) {
    handler = channel.pipeline().get(WindowsManagerHandler.class);
  }

  public static void main(String[] args) throws ConnectTimeoutException, InterruptedException {

    final ProxyClient proxyClient = new ProxyClient("192.168.1.76", 4198);
    proxyClient.connect();
//    byte[] address = new byte[]{0x33, 0x44, 0x55};
    Thread.sleep(2000);

    byte[] address = new byte[]{0x33, 0x44, 0x55};
    byte[] address1 = new byte[]{0x33, 0x44, 0x63};
    byte[] address2 = new byte[]{0x33, 0x44, 0x61};
    byte[] address3 = new byte[]{0x33, 0x44, 0x60};
    byte[] address4 = new byte[]{0x33, 0x44, 0x58};
    byte[] address5 = new byte[]{0x33, 0x44, 0x59};
    byte[] address6 = new byte[]{0x33, 0x44, 0x62};
    byte[] address7 = new byte[]{0x33, 0x44, 0x57};
    byte[] address8 = new byte[]{0x33, 0x44, 0x64};
    byte[][] by = {address, address1, address2, address3, address4, address5, address6, address7,
        address8};
    for (int i = 0; i < 1; i++) {
      try {

//        proxyClient.openWindow(address);
//        Thread.sleep(15000);
//        proxyClient.closeWindow(address);
//        Thread.sleep(15000);
//        System.out.println(proxyClient.queryWindow(address));
//        Thread.sleep(200);
//        System.out.println(proxyClient.queryWindow(address));
//        Thread.sleep(200);
//        System.out.println(proxyClient.queryWindow(address));
//        Thread.sleep(200);
        int k = 0;
        for (byte[] bytes : by) {
          System.out.println(proxyClient.queryWindow(bytes));
          System.out.println("============================"+new Date().toLocaleString()+" 第"+((k++)+1)+"条====================================");

        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
//      try {
//        System.out.println( "exit program");
////        Thread.sleep(1000);
//        proxyClient.closeWindow(address);
//
//      } catch (ConnectTimeoutException e) {
//        System.out.println(e);
////        e.printStackTrace();
////        continue;
//      }
  }

//  }
}