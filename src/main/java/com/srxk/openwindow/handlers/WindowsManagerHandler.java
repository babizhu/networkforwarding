package com.srxk.openwindow.handlers;

import com.srxk.openwindow.ProxyClient;
import com.srxk.openwindow.cmd.Cmd;
import com.srxk.openwindow.pojo.WindowStatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WindowsManagerHandler extends BaseHandler {

  public WindowsManagerHandler(ProxyClient client) {
    super(client);
  }

  /**
   * 开窗
   */
  public ChannelFuture openWindow(byte[] address) {
    System.out.println("========================================");
    System.out.println("open window");
    System.out.println( channel );
    System.out.println( channel.isOpen() );
    System.out.println("========================================");
    final ByteBuf request = buildCmd(channel.alloc().buffer(), Cmd.OPEN_WINDOW, address);
    return channel.writeAndFlush(request);
//    return null;
//    final Object result = waitResult();
//    return result;
  }

  public ChannelFuture closeWindow(byte[] address) {
    System.out.println("================="+channel.hashCode()+"=======================");
    System.out.println("close window");

    System.out.println( channel );
    System.out.println( channel.isOpen() );
    System.out.println("========================================");
    final ByteBuf request = buildCmd(channel.alloc().buffer(), Cmd.CLOSE_WINDOW, address);
     return channel.writeAndFlush(request);
  }

  public ChannelFuture stopWindow(byte[] address) {
    System.out.println("========================================");
    System.out.println("stop window");

    System.out.println( channel );
    System.out.println( channel.isOpen() );
    System.out.println("========================================");
    final ByteBuf request = buildCmd(channel.alloc().buffer(), Cmd.STOP_WINDOW, address);
    return channel.writeAndFlush(request);
  }

  public WindowStatus queryWindow(byte[] address) {
//    System.out.println("========================================");
    System.out.println(new Date().toLocaleString() + Arrays.toString(address) + "send");

//    System.out.println( channel );
//    System.out.println( channel.isOpen() );
//    System.out.println("========================================");
    final ByteBuf request = buildCmd(channel.alloc().buffer(), Cmd.QUERY_WINDOW, address);
    channel.writeAndFlush(request);
    isPending.set(true);
    int delaySecond = 3;
    scheduledFuture = channel.eventLoop().schedule(() -> {
      if (isPending.compareAndSet(true, false)) {
        answer.add(WindowStatus.EMPTY);
        System.out.println(new Date().toLocaleString() + Arrays.toString(address) + "Time out");
      }
    }, delaySecond, TimeUnit.SECONDS);
//    scheduledFuture.can
    final WindowStatus result = waitResult();

    return result;
  }
}
