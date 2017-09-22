package com.srxk.openwindow.handlers;



import static com.srxk.openwindow.handlers.BaseHandler.formatByteBuf;

import com.srxk.openwindow.pojo.WindowStatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class MsgDecoder extends LengthFieldBasedFrameDecoder {
//    private static final Logger LOG = LoggerFactory.getLogger( MessageDecoder.class );

  private final static int MAX_FRAME_LENGTH = 50,
      LENGTH_FILED_OFFSET = 1,
      LENGTH_ADJUSTMENT = -1,
      LENGTH_FIELD_LENGTH = 1;//真实环境
//            LENGTH_FIELD_LENGTH = 2;//大包压力测试环境

  public MsgDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
    super(maxFrameLength, lengthFieldOffset, lengthFieldLength, LENGTH_ADJUSTMENT, 0);
  }

  public MsgDecoder() {
    this(MAX_FRAME_LENGTH, LENGTH_FILED_OFFSET, LENGTH_FIELD_LENGTH);
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    System.out.println( formatByteBuf( ctx, "read", in ) );
    final ByteBuf frame = (ByteBuf) super.decode(ctx, in);
    if (frame == null) {
      return null;
    }
    byte head = frame.readByte();
    if (head != BaseHandler.HEAD) {
      return null;
    }
    short len = frame.readUnsignedByte();//真实环境
//    short cmdId = frame.readUnsignedByte();
//    int dataLen = frame.writerIndex()  - frame.readerIndex();
//    ByteBuf data = frame.slice( frame.readerIndex(), dataLen );

    byte[] address = new byte[3];
    frame.readBytes(address);

    byte[] resultArr = new byte[3];
    frame.readBytes(resultArr);
    byte result;
    if (resultArr[0] == 1&&resultArr[1] == 0 &&resultArr[2] == 0 ) {
      result = 1;//开
    } else if (resultArr[0] == 0&&resultArr[1] ==1 &&resultArr[2] == 0 ) {
      result = 2;//停
    } else if (resultArr[0] == 0&&resultArr[1] == 0 &&resultArr[2] == 1 ) {
      result = 3;//关
    }else {
      result = 4;//出问题了

  }

    return new WindowStatus(address,result);

}

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
    ctx.close();
  }
}
