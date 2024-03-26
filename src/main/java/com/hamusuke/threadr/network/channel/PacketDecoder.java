package com.hamusuke.threadr.network.channel;

import com.hamusuke.threadr.network.protocol.PacketDirection;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PacketDirection direction;

    public PacketDecoder(PacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        if (i != 0) {
            var buf = new IntelligentByteBuf(in);
            int j = buf.readVariableInt();
            var packet = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get().createPacket(this.direction, j, buf);
            if (packet == null) {
                throw new IOException("Bad packet id: " + j);
            } else {
                if (buf.readableBytes() > 0) {
                    LOGGER.warn("Packet " + packet.getClass().getSimpleName() + " was larger than expected, found " + buf.readableBytes());
                } else {
                    out.add(packet);
                }
            }
        }
    }
}
