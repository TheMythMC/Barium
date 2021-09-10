package dev.themyth.barium;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Barium implements ModInitializer {
    public static LinkedList<ModContainer> mods = new LinkedList<>();
    public List<File> ignoredMods = new LinkedList<>();
    private final String[] gameVersions = {"1.14", "1.15", "1.16", "1.17"};

    @Override
    public void onInitialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::install));
    }

    public void install() {
        // Mods folder will be completely removed besides the ignored (aka not on curse or modrinth)
        Arrays.stream(
                Objects.requireNonNull
                (new File(FabricLoader.getInstance().getGameDir().resolve("mods").toString()).listFiles())).forEach(
                file -> {
                    if (file.isDirectory() || ignoredMods.contains(file)) return;

                    file.delete();
                }
        );
        // Now we just have to move every mod from mods/temp/ to mods/ and delete temp/
        Arrays.stream(Objects.requireNonNull(new File(FabricLoader.getInstance().getGameDir().resolve("mods").resolve("temp").toFile().getAbsolutePath()).listFiles()))
                .forEach( file -> {
                    try {
                        Files.move(Paths.get(file.getAbsolutePath()), FabricLoader.getInstance().getGameDir().resolve("mods"), StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        new File(FabricLoader.getInstance().getGameDir().resolve("mods").resolve("temp").toAbsolutePath().toString()).delete();
    }

    public void stripOfVersions(String modName) {
        String strippedModName = ""; // should never be null
        for (String string : modName.split("/")) {
            AtomicBoolean shouldStop = new AtomicBoolean(false);
            Arrays.stream(gameVersions).forEach(version -> {
                if (string.contains(version))
                    shouldStop.set(true);
            });
            if(shouldStop.get())
                break;
        }
    }

    static {
        FabricLoader.getInstance().getAllMods().stream().map(mod -> mods.add(mod));
    }
}
