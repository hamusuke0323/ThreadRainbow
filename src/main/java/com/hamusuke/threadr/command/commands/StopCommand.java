package com.hamusuke.threadr.command.commands;

import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.command.Commands;
import com.mojang.brigadier.CommandDispatcher;

public class StopCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("stop").executes(c -> {
            if (true/*c.getSource().getServer().isHost(c.getSource().getSender())*/) {
                c.getSource().getServer().sendMessageToAll("サーバーを停止します");
                c.getSource().getServer().stop(false);
            } else {
                c.getSource().sendError("ホストのみこのコマンドを実行できます");
            }

            return 1;
        }));
    }
}
