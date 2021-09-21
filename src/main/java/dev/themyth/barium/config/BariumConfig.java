package dev.themyth.barium.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class BariumConfig {
    public List<String> ignoredMods;
    public long msBetweenUpdates;
    public FileConfig config;
    public BariumConfig() throws IOException {
        Path configFilePath = FabricLoader.getInstance().getConfigDir().resolve("barium").resolve("barium.json");
        if (!configFilePath.toFile().exists())
            configFilePath.toFile().createNewFile();
        config = FileConfig.builder(configFilePath).autosave().build();
        config.load();
        ignoredMods = Arrays.stream((String[])config.get("ignored_mods")).toList();
        msBetweenUpdates = config.get("auto_update_time") == null ? 0 : config.get("auto_update_time");
    }
}
