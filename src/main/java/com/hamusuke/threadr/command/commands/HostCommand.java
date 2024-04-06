package com.hamusuke.threadr.command.commands;

import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.command.Commands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class HostCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("host").then(Commands.argument("name", StringArgumentType.string()).executes(c -> {
            return changeHost(c.getSource(), StringArgumentType.getString(c, "name"));
        })));
    }

    private static int changeHost(CommandSource source, String name) {
        /*
        if (source.getServer().getSpiderManager().isHost(name)) {
            source.sendError("そのクモは既にホストです");
        } else if (source.getServer().isHost(source.getSender())) {
            if (source.getServer().getSpiderManager().changeHost(name)) {
                source.sendFeedback("ホストを " + name + " に変更しました");
            } else {
                source.sendError("クモが見つかりませんでした");
            }
        } else {
            source.sendError("ホストのみこのコマンドを実行できます");
        }

         */

        return 1;
    }
}
