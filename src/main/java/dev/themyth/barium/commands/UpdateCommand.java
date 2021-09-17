package dev.themyth.barium.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.themyth.barium.Barium;
import dev.themyth.barium.Updater;
import dev.themyth.barium.util.Downloader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import java.io.IOException;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class UpdateCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("update").requires(player -> player.hasPermissionLevel(4)).executes(ctx -> {
            this.update(ctx.getSource().getPlayer());
            return 0;
        }));
    }
    public void update(PlayerEntity player) {
        Objects.requireNonNull(Updater.fetchAllMods(player)).forEach((triplet) -> {
            Barium.sendMessage("Updating " + triplet.getA().replace(".jar", "") + "...", player);
            triplet.getC().delete();
            try {
                Downloader.downloadFile(triplet.getB(), FabricLoader.getInstance().getGameDir().resolve("mods").resolve(triplet.getA()).toString());
            } catch (IOException e) {
                Barium.sendMessage("Unable to update mod: " + triplet.getA().replace(".jar", ""), player);
            }
        });
    }
}
