package com.hamusuke.threadr.network.channel;

import com.hamusuke.threadr.network.PacketLogger;
import com.hamusuke.threadr.network.PacketLogger.PacketDetails;
import com.hamusuke.threadr.network.protocol.PacketDirection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PacketDirection direction;
    private final PacketLogger logger;

    public PacketDecoder(PacketDirection direction, PacketLogger logger) {
        this.direction = direction;
        this.logger = logger;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        if (i != 0) {
            var buf = new IntelligentByteBuf(in);
            int j = buf.readVariableInt();
            var packet = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get().createPacket(this.direction, j, buf);
            if (packet == null) {
                LOGGER.warn("Bad packet id: {}", j);
                return;
            }

            if (buf.readableBytes() > 0) {
                LOGGER.warn("Packet {} was larger than expected, found {}", packet.getClass().getSimpleName(), buf.readableBytes());
            } else {
                out.add(packet);
                this.logger.receive(new PacketDetails(packet, i));
            }
        }
    }
}
