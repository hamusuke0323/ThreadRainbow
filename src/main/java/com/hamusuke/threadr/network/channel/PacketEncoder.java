package com.hamusuke.threadr.network.channel;

import com.hamusuke.threadr.network.PacketLogger;
import com.hamusuke.threadr.network.PacketLogger.PacketDetails;
import com.hamusuke.threadr.network.protocol.PacketDirection;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PacketDirection direction;
    private final PacketLogger logger;

    public PacketEncoder(PacketDirection direction, PacketLogger logger) {
        this.direction = direction;
        this.logger = logger;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> msg, ByteBuf out) throws Exception {
        var protocol = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
        if (protocol == null) {
            throw new RuntimeException("Protocol unknown: " + msg);
        } else {
            var integer = protocol.getPacketId(this.direction, msg);

            if (integer == null) {
                LOGGER.warn("Can't serialize unregistered packet: {}", msg.getClass().getName());
                return;
            }

            var buf = new IntelligentByteBuf(out);
            buf.writeVariableInt(integer);
            int i = buf.writerIndex();
            msg.write(buf);
            int j = buf.writerIndex() - i;
            if (j > PacketInflater.MAXIMUM_UNCOMPRESSED_LENGTH) {
                throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than " + PacketInflater.MAXIMUM_UNCOMPRESSED_LENGTH + "): " + msg);
            }

            this.logger.send(new PacketDetails(msg, buf.readableBytes()));
        }
    }
}
