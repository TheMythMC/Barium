package dev.themyth.barium.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.themyth.barium.Barium;
import dev.themyth.barium.Updater;
import dev.themyth.barium.util.Downloader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import oshi.util.tuples.Triplet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class UpdateCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("update").requires(player -> player.hasPermissionLevel(4)).executes(ctx -> {
            ctx.getSource().sendFeedback(new LiteralText("Do you really want to delete all mods and reinstall? If so, run /update confirm"), true);
            return 1;
        }).then(literal("confirm").executes(ctx -> {
            update(ctx.getSource().getPlayer());
            return 1;
        })));
    }
    public void update(PlayerEntity player) {
        Barium.sendMessage("Fetching mods...", player);
        List<Triplet<String, URL, File>> allMods = Updater.fetchAllMods(player);
        if(allMods != null)
            allMods.forEach(triplet -> {
                if(Barium.config.ignoredMods.contains(triplet.getC().getName())) return;
                triplet.getC().delete();
                Downloader.downloadFile(triplet.getB(), triplet.getA());
            });
    }
}
