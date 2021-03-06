package dev.themyth.barium;

import com.mojang.bridge.game.GameVersion;
import dev.themyth.barium.commands.UpdateCommand;
import dev.themyth.barium.config.BariumConfig;
import dev.themyth.barium.config.BariumConfigImpl;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

// basically a utility class
// needs refactor, see ⬇
// TODO: REFACTOR
public class Barium implements ModInitializer {
    public static BariumConfig config;
    public static Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        try {
            this.registerHooks();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.registerCommands();
    }

    private void registerCommands() {
        // GUIs are hard
        // if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || !FabricLoader.getInstance().isModLoaded("modmenu")) {}
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> new UpdateCommand().register(dispatcher));
    }
    // Initializer so I don't have to call new Barium() in onInitialize
    static {
        LOGGER.info("Loading config...");
        AutoConfig.register(BariumConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BariumConfig.class).getConfig();
    }

    public static String getMinecraftVersion() {
        GameVersion minecraftVersion = MinecraftVersion.create();

        String verStr = minecraftVersion.getId();
        String[] verSplit = verStr.split("\\.");
        try {
            verSplit = ArrayUtils.remove(verSplit, 2);
        } catch (Exception ignored) {}
        verStr = verSplit[0] + verSplit[1];
        return verStr;
    }

    public void registerHooks() throws IOException {
        if (Barium.config.msBetweenUpdates == -1 ) {
            LOGGER.log(Level.WARN, "No AutoUpdate time set, not updating.");
            return;
        } else
            if (FabricLoader.getInstance().getConfigDir().resolve("barium").resolve("run_file").toFile().exists()) {
                LOGGER.log(Level.ERROR, "Already updating, request ignored. Please tell me how you encountered this message");
                return;
            }
        // save the config on close anyway

    }
}
