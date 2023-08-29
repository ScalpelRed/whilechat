package com.scalpelred.whilechat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class WhileChatCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, WhileChat whileChat){
        dispatcher.register(Commands.literal("whilechat")
                .then(Commands.literal("reload").then(Commands.argument("name", StringArgumentType.string()))
                        .executes(context -> reload(whileChat, context)))
                .then(Commands.literal("list")));
    }

    private static int reload(WhileChat whileChat, CommandContext<CommandSourceStack> context){
        if (whileChat.loadProgram(StringArgumentType.getString(context, "name"))) return 1;
        return 0;
    }


}
