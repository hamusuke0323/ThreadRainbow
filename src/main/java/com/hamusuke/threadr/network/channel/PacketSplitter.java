package com.hamusuke.threadr.network.channel;

import com.hamusuke.threadr.network.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class PacketSplitter extends ByteToMessageDecoder {
    private static final int MAX_VARINT21_BYTES = 3;
    private final ByteBuf helperBuf = Unpooled.directBuffer(MAX_VARINT21_BYTES);

    private static boolean copyVarInt(ByteBuf in, ByteBuf buf) {
        for (int i = 0; i < MAX_VARINT21_BYTES; ++i) {
            if (!in.isReadable()) {
                return false;
            }

            byte b = in.readByte();
            buf.writeByte(b);
            if (!VarInt.hasContinuationBit(b)) {
                return true;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) {
        this.helperBuf.release();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        in.markReaderIndex();
        this.helperBuf.clear();
        if (!copyVarInt(in, this.helperBuf)) {
            in.resetReaderIndex();
        } else {
            int i = VarInt.read(this.helperBuf);
            if (in.readableBytes() < i) {
                in.resetReaderIndex();
            } else {
                out.add(in.readBytes(i));
            }
        }
    }
}
