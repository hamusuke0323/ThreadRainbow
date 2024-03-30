package com.hamusuke.threadr.server;

import com.hamusuke.threadr.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainServer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws UnknownHostException {
        var s = InetAddress.getLocalHost().getHostAddress();
        int i = 16160;
        for (var arg : args) {
            if (arg.contains(":")) {
                var kv = arg.split(":");
                switch (kv[0]) {
                    case "address":
                        s = kv[1];
                        break;
                    case "port":
                        i = Integer.parseInt(kv[1]);
                }
            }
        }
        var host = s;
        int port = i;
        final var server = ThreadRainbowServer.startServer(thread -> new DedicatedServer(thread, host, port));
        var thread = new Thread(() -> server.stop(true), "Server Shutdown Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
