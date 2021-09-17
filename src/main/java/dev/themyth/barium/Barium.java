package dev.themyth.barium;

import com.mojang.bridge.game.GameVersion;
import dev.themyth.barium.commands.UpdateCommand;
import dev.themyth.barium.config.BariumConfig;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// basically a utility class
public class Barium implements ModInitializer {
    public static BariumConfig config;

    @Override
    public void onInitialize() {
        this.registerCommands();
    }

    private void registerCommands() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || FabricLoader.getInstance().isModLoaded("modmenu"))
            CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> new UpdateCommand().register(dispatcher)));
    }
    // Initializer so I don't have to call new Barium() in onInitialize
    static {
        config = new BariumConfig();
    }

    public static void sendMessage(String message, PlayerEntity player) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && MinecraftClient.getInstance().currentScreen != null) {
            InfoUtils.showGuiAndInGameMessage(Message.MessageType.INFO, message);
        } else {
            if (player != null) {
                player.sendMessage(new LiteralText(message), false);

            } else {
                Logger LOGGER = LoggerFactory.getLogger(Barium.class);
                LOGGER.info(message);
            }
        }
    }

    public static String getMinecraftVersion() {
        GameVersion minecraftVersion = MinecraftVersion.create();

        String verStr = minecraftVersion.getId();
        String[] verSplit = verStr.split("\\.");
        try {
            verSplit = ArrayUtils.remove(verSplit, 2);
        } catch (Exception e) {}
        verStr = verSplit[0] + verSplit[1];
        return verStr;
    }

}
