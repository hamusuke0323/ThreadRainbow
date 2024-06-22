package com.hamusuke.threadr.util;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.PingReq;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.RTTChangeNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.LobbyPongRsp;
import com.hamusuke.threadr.network.protocol.packet.clientbound.login.AliveRsp;
import com.hamusuke.threadr.network.protocol.packet.serverbound.common.PongRsp;
import com.hamusuke.threadr.network.protocol.packet.serverbound.lobby.LobbyPingReq;
import com.hamusuke.threadr.network.protocol.packet.serverbound.login.AliveReq;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class PacketUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final List<String> LOOP_PACKETS = Lists.newArrayList(
            AliveReq.class.getSimpleName(),
            AliveRsp.class.getSimpleName(),
            LobbyPingReq.class.getSimpleName(),
            LobbyPongRsp.class.getSimpleName(),
            PongRsp.class.getSimpleName(),
            PingReq.class.getSimpleName(),
            RTTChangeNotify.class.getSimpleName()
    );

    public static String getPacketDetails(Packet<?> packet, String byteStr) {
        var buf = new StringBuilder(packet.getClass().getSimpleName() + byteStr).append('\n');

        Arrays.asList(packet.getClass().getDeclaredFields()).forEach(field -> {
            try {
                field.setAccessible(true);
                buf.append(field.getName()).append(" = ");
                var obj = field.get(packet);
                if (obj instanceof String) {
                    buf.append(String.format("\"%s\"", obj));
                } else {
                    buf.append(obj);
                }

                buf.append(";\n");
            } catch (Exception e) {
                LOGGER.warn("Failed to access the field", e);
            }
        });

        return buf.toString();
    }

    public static String convertBytes(long bytes) {
        if (bytes < 0) {
            return convertBytes(Long.MAX_VALUE);
        }

        var curSize = Size.B;
        var remainBytes = (double) bytes;

        while ((remainBytes / 1024.0D) >= 1.0D && curSize.next() != null) {
            curSize = curSize.next();
            remainBytes /= 1024.0D;
        }

        return "%.1f %s".formatted(remainBytes, curSize);
    }

    private enum Size {
        B,
        KB,
        MB,
        GB,
        TB,
        PB,
        EB;

        @Nullable
        private Size next() {
            var next = this.ordinal() + 1;
            var v = values();
            return this == EB ? null : v[next];
        }
    }
}
