package com.hamusuke.threadr.network.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.zip.Deflater;

public class PacketDeflater extends MessageToByteEncoder<ByteBuf> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int MAXIMUM_COMPRESSED_LENGTH = 2097152;
    private final byte[] encodeBuf = new byte[8192];
    private final Deflater deflater;
    private int threshold;

    public PacketDeflater(int threshold) {
        this.threshold = threshold;
        this.deflater = new Deflater();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        int i = msg.readableBytes();
        var byteBuf = new IntelligentByteBuf(out);
        if (i < this.threshold) {
            byteBuf.writeVariableInt(0);
            byteBuf.writeBytes(msg);
        } else {
            if (i > MAXIMUM_COMPRESSED_LENGTH) {
                msg.markReaderIndex();
                LOGGER.error("Attempted to send packet over maximum protocol size: {} > {}}", i, MAXIMUM_COMPRESSED_LENGTH);
                msg.resetReaderIndex();
            }
            var bytes = new byte[i];
            msg.readBytes(bytes);
            byteBuf.writeVariableInt(bytes.length);
            this.deflater.setInput(bytes, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.encodeBuf);
                byteBuf.writeBytes(this.encodeBuf, 0, j);
            }

            this.deflater.reset();
        }
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
