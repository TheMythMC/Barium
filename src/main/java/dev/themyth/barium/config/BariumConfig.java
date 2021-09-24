package dev.themyth.barium.config;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BariumConfig {
    public List<String> ignoredMods;
    public static BariumConfig INSTANCE;
    public long msBetweenUpdates;
    public long lastUpdate;
    public static FileConfig config;
    public BariumConfig() throws IOException {
        Path configPath = Paths.get("config/barium/barium.json");
        if (!configPath.toFile().exists())
            if (!configPath.toFile().createNewFile())
                throw new IOException();

        config = FileConfig.builder(configPath).concurrent().autosave().build();

        ignoredMods = List.of(config.getOrElse("ignored_mods", new String[]{}));
        // Will update every shutdown
        msBetweenUpdates = config.getOrElse("ms_between_updates", 0);
        // never had an update
        lastUpdate = config.getOrElse("last_update", -1);
    }

    public static void save() {
        config.set("ignored_mods", INSTANCE.ignoredMods);
        config.set("ms_between_updates", INSTANCE.msBetweenUpdates);
        config.set("last_update", INSTANCE.lastUpdate);
    }

    static {
        try {
            INSTANCE = new BariumConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
