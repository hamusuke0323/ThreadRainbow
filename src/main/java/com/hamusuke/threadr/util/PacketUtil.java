package com.hamusuke.threadr.util;

import com.hamusuke.threadr.network.protocol.packet.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class PacketUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    public static String getPacketDetails(Packet<?> packet) {
        var buf = new StringBuilder(packet.getClass().getSimpleName()).append('\n');

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
}
