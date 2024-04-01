package com.hamusuke.threadr.server.dedicated;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.Constants;
import com.hamusuke.threadr.server.ThreadRainbowServer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class DedicatedServer extends ThreadRainbowServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> commandQueue = Collections.synchronizedList(Lists.newArrayList());

    public DedicatedServer(Thread serverThread, String host, int port) {
        super(serverThread);
        this.setServerIp(host);
        this.setServerPort(port);
    }

    @Override
    protected boolean setupServer() throws IOException {
        Thread thread = new Thread(this::run, "Server console handler");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        thread.start();
        LOGGER.info("Starting thread rainbow server version {}", Constants.VERSION);
        InetAddress inetAddress = null;
        if (!this.getServerIp().isEmpty()) {
            inetAddress = InetAddress.getByName(this.getServerIp());
        }
        this.generateKeyPair();
        LOGGER.info("Starting thread rainbow server on {}:{}", this.getServerIp().isEmpty() ? "*" : this.getServerIp(), this.getServerPort());
        this.getNetworkIo().bind(inetAddress, this.getServerPort());
        LOGGER.info("Done! Type '/stop' to stop the server!");

        return true;
    }

    private void run() {
        var bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        String string;
        try {
            while (!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
                if (string.startsWith("/")) {
                    DedicatedServer.this.enqueueCommand(string.substring(1));
                } else {
                    this.sendMessageToAll(string);
                }
            }
        } catch (IOException var4) {
            DedicatedServer.LOGGER.error("Exception handling console input", var4);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.runQueuedCommands();
    }

    public void enqueueCommand(String command) {
        this.commandQueue.add(command);
    }

    public void runQueuedCommands() {
        while (!this.commandQueue.isEmpty()) {
            String command = this.commandQueue.remove(0);
            this.runCommand(command);
        }
    }

    private void runCommand(String command) {
        try {
            this.dispatcher.execute(command, this);
        } catch (CommandSyntaxException e) {
            this.sendError(e.getMessage());
        }
    }
}
