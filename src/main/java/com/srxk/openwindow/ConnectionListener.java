package com.srxk.openwindow;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionListener implements ChannelFutureListener {
  private ProxyClient client;
  public ConnectionListener(ProxyClient client) {
    this.client = client;
  }
  @Override
  public void operationComplete(ChannelFuture channelFuture) throws Exception {
    if (!channelFuture.isSuccess()) {
      System.out.println("Reconnect");
      final EventLoop loop = channelFuture.channel().eventLoop();
      loop.schedule(() -> {
        client.createBootstrap(new Bootstrap(), loop);
      }, 1L, TimeUnit.SECONDS);
    }else {
      log.debug("链接成功");
      client.connectSuccess(channelFuture.channel());
//      client.setChannel(channelFuture.channel());
    }
  }
}
