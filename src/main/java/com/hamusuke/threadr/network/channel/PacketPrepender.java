package com.hamusuke.threadr.network.channel;

import com.hamusuke.threadr.network.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {
    private static final int MAX = 3;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        int i = msg.readableBytes();
        int j = VarInt.getByteSize(i);
        if (j > MAX) {
            throw new IllegalArgumentException("unable to fit " + i + " into " + MAX);
        } else {
            out.ensureWritable(j + i);
            VarInt.write(out, i);
            out.writeBytes(msg, msg.readerIndex(), i);
        }
    }
}
