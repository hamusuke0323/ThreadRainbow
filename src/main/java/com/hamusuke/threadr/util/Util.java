package com.hamusuke.threadr.util;

import com.google.common.util.concurrent.MoreExecutors;
import com.hamusuke.threadr.network.channel.IntelligentByteBuf;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Util {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final ExecutorService MAIN_WORKER_EXECUTOR = createWorker("Main");
    private static final ExecutorService IO_WORKER_EXECUTOR = createIoWorker();
    private static final String ALGORITHM = "SHA-256";
    public static LongSupplier nanoTimeSupplier = System::nanoTime;

    public static String toHTML(String s) {
        return "<html>" + s.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>";
    }

    public static File avoidDuplicatingDirectoryName(File dir, String dirName) {
        File file = new File(dir, dirName);
        int i = 1;
        while (file.isDirectory() && file.exists()) {
            file = new File(dir, dirName + "-" + i);
            i++;
        }

        return file;
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T makeAndAccess(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static String hash(String plane) throws NoSuchAlgorithmException {
        return new String(MessageDigest.getInstance(ALGORITHM).digest(plane.getBytes(StandardCharsets.UTF_8)));
    }

    private static ExecutorService createWorker(String name) {
        int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxBackgroundThreads());
        ExecutorService executorService;
        if (i <= 0) {
            executorService = MoreExecutors.newDirectExecutorService();
        } else {
            executorService = new ForkJoinPool(i, forkJoinPool -> {
                ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool) {
                    protected void onTermination(Throwable throwable) {
                        if (throwable != null) {
                            Util.LOGGER.warn(this.getName() + " died", throwable);
                        } else {
                            Util.LOGGER.debug("{} shutdown", this.getName());
                        }

                        super.onTermination(throwable);
                    }
                };
                forkJoinWorkerThread.setName("Worker-" + name + "-" + NEXT_WORKER_ID.getAndIncrement());
                return forkJoinWorkerThread;
            }, Util::uncaughtExceptionHandler, true);
        }

        return executorService;
    }

    private static ExecutorService createIoWorker() {
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("IO-Worker-" + NEXT_WORKER_ID.getAndIncrement());
            thread.setUncaughtExceptionHandler(Util::uncaughtExceptionHandler);
            return thread;
        });
    }

    private static void uncaughtExceptionHandler(Thread thread, Throwable throwable) {
        LOGGER.error("Caught exception in thread " + thread, throwable);
    }

    private static int getMaxBackgroundThreads() {
        String string = System.getProperty("max.bg.threads");
        if (string != null) {
            try {
                int i = Integer.parseInt(string);
                if (i >= 1 && i <= 255) {
                    return i;
                }

                LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", string, 255});
            } catch (NumberFormatException var2) {
                LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", string, 255});
            }
        }

        return 255;
    }

    public static void shutdownExecutors() {
        attemptShutdown(MAIN_WORKER_EXECUTOR);
        attemptShutdown(IO_WORKER_EXECUTOR);
    }

    private static void attemptShutdown(ExecutorService service) {
        service.shutdown();

        boolean bl;
        try {
            bl = service.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            bl = false;
        }

        if (!bl) {
            service.shutdownNow();
        }
    }

    public static ExecutorService getMainWorkerExecutor() {
        return MAIN_WORKER_EXECUTOR;
    }

    public static ExecutorService getIoWorkerExecutor() {
        return IO_WORKER_EXECUTOR;
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

    public static void pack(BufferedImage bufferedImage, IntelligentByteBuf buf) throws IOException {
        FastByteArrayOutputStream fast = new FastByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", fast);
        buf.writeByteArray(fast.array);
    }

    public static BufferedImage unpack(IntelligentByteBuf buf) throws IOException {
        FastByteArrayInputStream fast = new FastByteArrayInputStream(buf.readByteArray());
        return ImageIO.read(fast);
    }
}
