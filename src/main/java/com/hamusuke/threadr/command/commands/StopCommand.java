package com.hamusuke.threadr.command.commands;

import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.command.Commands;
import com.mojang.brigadier.CommandDispatcher;

public class StopCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("stop").executes(c -> {
            if (c.getSource().getSender() == null) {
                c.getSource().sendMessageToAll("サーバーを停止します");
                c.getSource().getServer().stop(false);
                return 1;
            } else {
                c.getSource().sendError("このコマンドはサーバーコンソールからのみ実行できます");
                return -1;
            }
        }));
    }
}
