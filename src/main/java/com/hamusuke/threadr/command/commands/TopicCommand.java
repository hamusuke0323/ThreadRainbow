package com.hamusuke.threadr.command.commands;

import com.google.common.collect.Lists;
import com.hamusuke.threadr.command.CommandSource;
import com.hamusuke.threadr.game.topic.Topic;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.util.List;

import static com.hamusuke.threadr.command.Commands.argument;
import static com.hamusuke.threadr.command.Commands.literal;

public class TopicCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("topic")
                        .then(literal("add")
                                .then(literal("2-lines")
                                        .then(argument("sub", StringArgumentType.string())
                                                .then(argument("main", StringArgumentType.string())
                                                        .then(argument("minDesc", StringArgumentType.string())
                                                                .then(argument("maxDesc", StringArgumentType.string())
                                                                        .executes(TopicCommand::add2LinesTopic))))))
                                .then(argument("main", StringArgumentType.string())
                                        .then(argument("minDesc", StringArgumentType.string())
                                                .then(argument("maxDesc", StringArgumentType.string())
                                                        .executes(TopicCommand::addTopic)))))
                        .then(literal("remove")
                                .then(argument("topicID", IntegerArgumentType.integer(0))
                                        .executes(TopicCommand::removeTopic)))
                        .then(literal("choose")
                                .then(argument("topicID", IntegerArgumentType.integer(0))
                                        .executes(TopicCommand::chooseTopic)))
        );
    }

    private static int add2LinesTopic(CommandContext<CommandSource> ctx) {
        var sub = StringArgumentType.getString(ctx, "sub");
        var main = StringArgumentType.getString(ctx, "main");
        var minDesc = StringArgumentType.getString(ctx, "minDesc");
        var maxDesc = StringArgumentType.getString(ctx, "maxDesc");

        sub = sub.trim().substring(0, Math.min(sub.length(), Topic.MAX_TEXT_LENGTH));
        main = main.trim().substring(0, Math.min(main.length(), Topic.MAX_TEXT_LENGTH));
        minDesc = minDesc.trim().substring(0, Math.min(minDesc.length(), Topic.MAX_TEXT_LENGTH));
        maxDesc = maxDesc.trim().substring(0, Math.min(maxDesc.length(), Topic.MAX_TEXT_LENGTH));

        var source = ctx.getSource();
        var sender = source.getSender();

        if (invalidate(ctx)) {
            return 0;
        }

        if (invalidateTexts(Lists.newArrayList(sub, main, minDesc, maxDesc))) {
            sender.sendError("文字列は空にできません");
            return 0;
        }

        var newTopic = new Topic(List.of(sub, main), minDesc, maxDesc);
        var entry = sender.curRoom.addCustomTopic(newTopic);
        sender.sendFeedback("以下のお題(ID:%d)を新しく追加しました\n%s".formatted(entry.id(), newTopic.toString()));

        return 1;
    }

    private static int addTopic(CommandContext<CommandSource> ctx) {
        var main = StringArgumentType.getString(ctx, "main");
        var minDesc = StringArgumentType.getString(ctx, "minDesc");
        var maxDesc = StringArgumentType.getString(ctx, "maxDesc");

        main = main.trim().substring(0, Math.min(main.length(), Topic.MAX_TEXT_LENGTH));
        minDesc = minDesc.trim().substring(0, Math.min(minDesc.length(), Topic.MAX_TEXT_LENGTH));
        maxDesc = maxDesc.trim().substring(0, Math.min(maxDesc.length(), Topic.MAX_TEXT_LENGTH));

        var sender = ctx.getSource().getSender();

        if (invalidate(ctx)) {
            return 0;
        }

        if (invalidateTexts(Lists.newArrayList(main, minDesc, maxDesc))) {
            sender.sendError("文字列は空にできません");
            return 0;
        }

        var newTopic = new Topic(List.of(main), minDesc, maxDesc);
        var entry = sender.curRoom.addCustomTopic(newTopic);
        sender.sendFeedback("以下のお題(ID:%d)を新しく追加しました\n%s".formatted(entry.id(), newTopic.toString()));

        return 1;
    }

    private static int removeTopic(CommandContext<CommandSource> ctx) {
        var id = IntegerArgumentType.getInteger(ctx, "topicID");
        var sender = ctx.getSource().getSender();

        if (invalidate(ctx)) {
            return 0;
        }

        var topic = sender.curRoom.removeTopic(id);
        if (topic == null) {
            sender.sendError("そのようなお題は存在しません");
            return 0;
        }

        sender.sendFeedback("以下のお題(ID:%d)をお題リストから削除しました\n%s".formatted(id, topic.toString()));

        return 1;
    }

    private static int chooseTopic(CommandContext<CommandSource> ctx) {
        var sender = ctx.getSource().getSender();

        if (invalidate(ctx)) {
            return 0;
        } else if (sender.curRoom.getGame() == null) {
            ctx.getSource().sendError("ゲームが始まっていません");
            return 0;
        }

        int id = IntegerArgumentType.getInteger(ctx, "topicID");
        var game = sender.curRoom.getGame();
        if (game.setTopic(id)) {
            ctx.getSource().sendCommandFeedback("お題を設定しました", true);
            return 1;
        }

        ctx.getSource().sendError("お題が見つかりませんでした");
        return 0;
    }

    private static boolean invalidateTexts(List<String> texts) {
        return texts.stream().anyMatch(s -> s.isEmpty() || s.isBlank());
    }

    private static boolean invalidate(CommandContext<CommandSource> ctx) {
        var sender = ctx.getSource().getSender();
        if (sender == null) {
            ctx.getSource().sendError("このコマンドはサーバーから実行できません");
            return true;
        } else if (sender.curRoom == null) {
            ctx.getSource().sendError("あなたはどこの部屋にも入っていません");
            return true;
        } else if (!sender.isHost()) {
            ctx.getSource().sendError("ホストのみこのコマンドを実行できます");
            return true;
        }

        return false;
    }
}
