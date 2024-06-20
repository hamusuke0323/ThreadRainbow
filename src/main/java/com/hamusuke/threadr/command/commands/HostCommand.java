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
        if (source.getSender() == null) {
            source.sendError("サーバーはこのコマンドを実行できません");
            return -1;
        }

        var sender = source.getSender();
        if (sender.curRoom == null || !sender.isHost()) {
            source.sendError("ホストのみこのコマンドを実行できます");
            return -1;
        }

        if (sender.curRoom.isHost(name)) {
            source.sendError("そのクモは既にホストです");
            return -1;
        }

        if (!sender.curRoom.doesSpiderExist(name)) {
            source.sendError("クモが見つかりませんでした");
            return -1;
        }

        if (sender.curRoom.shouldNotBeHost(name)) {
            source.sendError("このクモは現在ゲームに参加していません\nゲームが進行できなくなる恐れがあります");
            return -1;
        }

        if (sender.curRoom.changeHost(name)) {
            source.sendFeedback("ホストを " + name + " に変更しました");
            return 1;
        }

        return -1;
    }
}
