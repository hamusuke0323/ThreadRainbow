package com.hamusuke.threadr.network;

import io.netty.buffer.ByteBuf;

public class VarInt {
    private static final int MAX_VARINT_SIZE = 5;
    private static final int DATA_BITS_MASK = 127;
    private static final int CONTINUATION_BIT_MASK = 128;
    private static final int DATA_BITS_PER_BYTE = 7;

    public static int getByteSize(int i2) {
        for (int i = 1; i < MAX_VARINT_SIZE; ++i) {
            if ((i2 & -1 << i * DATA_BITS_PER_BYTE) == 0) {
                return i;
            }
        }

        return MAX_VARINT_SIZE;
    }

    public static boolean hasContinuationBit(byte b) {
        return (b & 128) == 128;
    }

    public static int read(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b;
        do {
            b = buf.readByte();
            i |= (b & DATA_BITS_MASK) << j++ * DATA_BITS_PER_BYTE;
            if (j > MAX_VARINT_SIZE) {
                throw new RuntimeException("VarInt too big");
            }
        } while (hasContinuationBit(b));

        return i;
    }

    public static ByteBuf write(ByteBuf buf, int i) {
        while ((i & -CONTINUATION_BIT_MASK) != 0) {
            buf.writeByte(i & DATA_BITS_MASK | CONTINUATION_BIT_MASK);
            i >>>= DATA_BITS_PER_BYTE;
        }

        buf.writeByte(i);
        return buf;
    }
}
