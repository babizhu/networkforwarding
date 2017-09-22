package com.srxk.openwindow;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.TimeUnit;

public class MyInboundHandler extends SimpleChannelInboundHandler {
  private ProxyClient client;
  public MyInboundHandler(ProxyClient client) {
    this.client = client;
  }
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    final EventLoop eventLoop = ctx.channel().eventLoop();
    eventLoop.schedule(() -> {
      client.createBootstrap(new Bootstrap(), eventLoop);
    }, 1L, TimeUnit.SECONDS);
    super.channelInactive(ctx);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

  }
}