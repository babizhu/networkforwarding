package com.srxk.openwindow.handlers;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

import com.srxk.openwindow.ProxyClient;
import com.srxk.openwindow.cmd.Cmd;
import com.srxk.openwindow.pojo.WindowStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseHandler extends SimpleChannelInboundHandler<WindowStatus> {
  private ProxyClient client;
  protected ScheduledFuture<?> scheduledFuture;
  public BaseHandler(ProxyClient client) {
    this.client = client;
  }
  /**
   * 是否有客户端正在调用queryWindow函数
   */
  protected AtomicBoolean isPending = new AtomicBoolean(false);
  public static final byte HEAD = (byte) 0X1A;
  public static final byte FOOT = (byte) 0X0F;

  protected final BlockingQueue<WindowStatus> answer = new LinkedBlockingQueue<>();
  protected Channel channel;

  protected WindowStatus waitResult(){
    boolean interrupted = false;
    WindowStatus response;
    for(; ; ) {
      try {

        response = answer.take();
//        System.out.println( "结果码 :" + ErrorCode.fromNum( response.getResultCode() ));
//        System.out.println( "Sequence :" + response.getSequence() );
        break;
      } catch( InterruptedException ignore ) {
        interrupted = true;
      }
    }

    if( interrupted ) {
      Thread.currentThread().interrupt();
    }
    return response;

  }

//  @Override
//  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//    super.channelRegistered(ctx);
//    this.channel = ctx.channel();
//  }
  @Override
  public void channelActive( ChannelHandlerContext ctx ){
//    this.ctx = ctx;
    System.out.println("BaseHandler.channelActive");
    channel = ctx.channel();


  }

//  @Override
//  public void channelRead( ChannelHandlerContext ctx, Object msg ){
//    log.debug("query Window");
//    if(isPending.compareAndSet(false,true)){
//      answer.add( msg );
//      System.out.println( formatByteBuf( ctx, "read", (ByteBuf) msg ) );
//    }else {
//      //丢弃数据
//    }
//
//  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    final EventLoop eventLoop = ctx.channel().eventLoop();
    eventLoop.schedule(() -> {
      client.createBootstrap(new Bootstrap(), eventLoop);
    }, 1L, TimeUnit.SECONDS);
    super.channelInactive(ctx);
  }
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, WindowStatus msg) throws Exception {
//    log.debug("收到了数据");
    if(isPending.compareAndSet(true,false)){
      answer.add( msg );
      if( scheduledFuture != null ){
        scheduledFuture.cancel(false);
      }
//      System.out.println("该我处理数据！！！！！！！！！！！！！！！！！！！！！！！！");
//      System.out.println( formatByteBuf( ctx, "read", msg ) );
    }
  }

  @Override
  public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ){
    cause.printStackTrace();
//    ctx.close();
  }

  /**
   * 构建各种命令包
   *
   * @param buffer        要填充的buffer
   * @param cmd           命令枚举
   * @return
   *                      填充好的buffer
   */
  ByteBuf buildCmd( ByteBuf buffer, Cmd cmd,byte[] address ){
    buffer.writeByte( HEAD );
    buffer.writeByte( 8 );//长度

    switch( cmd ) {
      case OPEN_WINDOW:
        buffer.writeBytes(address);
        buffer.writeBytes(new byte[]{0x0,0x0,0x1});
        break;
      case CLOSE_WINDOW:
        buffer.writeBytes(address);
        buffer.writeBytes(new byte[]{0x0,0x02,0x1});
        break;
      case STOP_WINDOW:
        buffer.writeBytes(address);
        buffer.writeBytes(new byte[]{0x0,0x01,0x1});
        break;
      case QUERY_WINDOW:
        buffer.writeBytes(address);
        buffer.writeBytes(new byte[]{(byte) 0x86, (byte) 0x86, (byte) 0x86});
        break;
      default:
        System.out.println( "未找到的命令:" + cmd );
    }

    buffer.writeByte( FOOT );
    return buffer;
  }

  /**
   * 格式化客户端反馈的信息，方便查看，来源netty源代码
   *
   * @param ctx           ctx
   * @param eventName     eventName
   * @param msg           msg
   * @return
   *                      方便查看的字符串
   */
  @SuppressWarnings("SameParameterValue")
  public static String formatByteBuf( ChannelHandlerContext ctx, String eventName, ByteBuf msg ){
    String chStr = ctx.channel().toString();
    int length = msg.readableBytes();
    if( length == 0 ) {
      return chStr + ' ' + eventName + ": 0B";
    } else {
      int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
      StringBuilder buf = new StringBuilder( chStr.length() + 1 + eventName.length() + 2 + 10 + 1 + 2 + rows * 80 );

      buf.append( chStr ).append( ' ' ).append( eventName ).append( ": " ).append( length ).append( 'B' ).append( NEWLINE );
      appendPrettyHexDump( buf, msg );

      return buf.toString();
    }
  }


}