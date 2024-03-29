package com.hamusuke.threadr.util;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Util {
    public static final LongSupplier nanoTimeSupplier = System::nanoTime;

    public static String toHTML(String s) {
        return "<html>" + s.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>";
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
