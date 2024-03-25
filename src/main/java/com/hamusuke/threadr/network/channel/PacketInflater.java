package com.hamusuke.threadr.network.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.zip.Inflater;

public class PacketInflater extends ByteToMessageDecoder {
    public static final int MAXIMUM_UNCOMPRESSED_LENGTH = 8388608;
    private final Inflater inflater;
    private int threshold;
    private boolean validateDecompressed;

    public PacketInflater(int threshold, boolean validate) {
        this.threshold = threshold;
        this.validateDecompressed = validate;
        this.inflater = new Inflater();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            var byteBuf = new IntelligentByteBuf(in);
            int i = byteBuf.readVariableInt();
            if (i == 0) {
                out.add(byteBuf.readBytes(byteBuf.readableBytes()));
            } else {
                if (this.validateDecompressed) {
                    if (i < this.threshold) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.threshold);
                    }

                    if (i > MAXIMUM_UNCOMPRESSED_LENGTH) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + MAXIMUM_UNCOMPRESSED_LENGTH);
                    }
                }

                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                this.inflater.setInput(bytes);
                byte[] bytes1 = new byte[i];
                this.inflater.inflate(bytes1);
                out.add(Unpooled.wrappedBuffer(bytes1));
                this.inflater.reset();
            }
        }
    }

    public void setThreshold(int threshold, boolean validate) {
        this.threshold = threshold;
        this.validateDecompressed = validate;
    }
}
