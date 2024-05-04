package com.hamusuke.threadr.util;

import com.hamusuke.threadr.network.listener.PacketListener;
import com.hamusuke.threadr.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.threadr.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.threadr.network.protocol.packet.Packet;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.DisconnectNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.lobby.LobbyDisconnectNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.login.LoginDisconnectNotify;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Util {
    public static final LongSupplier nanoTimeSupplier = System::nanoTime;

    public static Packet<?> toDisconnectPacket(PacketListener listener, String msg) {
        if (listener instanceof ServerLoginPacketListener) {
            return new LoginDisconnectNotify(msg);
        } else if (listener instanceof ServerLobbyPacketListener) {
            return new LobbyDisconnectNotify(msg);
        } else {
            return new DisconnectNotify(msg);
        }
    }

    public static String toHTML(String s) {
        return "<html>" + s.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>";
    }

    public static String numberOnly(String s) {
        return filterBy(s, value -> '0' <= value && value <= '9');
    }

    public static String filterBy(String s, IntPredicate intPredicate) {
        var buf = new StringBuilder();
        s.chars().filter(intPredicate).forEach(value -> buf.append((char) value));
        return buf.toString();
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T makeAndAccess(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static long getMeasuringTimeMs() {
        return getMeasuringTimeNano() / 1000000L;
    }

    public static long getMeasuringTimeNano() {
        return nanoTimeSupplier.getAsLong();
    }

    public static <E> E chooseRandom(List<E> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
}
