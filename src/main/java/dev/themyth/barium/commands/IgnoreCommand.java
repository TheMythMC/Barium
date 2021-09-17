package dev.themyth.barium.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class IgnoreCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("ignore").then(argument("modname", ArgumentType<String>)))
    }
}
