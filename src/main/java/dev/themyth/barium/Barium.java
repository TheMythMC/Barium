// TODO: REFACTOR
package dev.themyth.barium;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Barium implements ModInitializer {
    /**
     * DON'T USE THIS
     */
    public static Barium INSTANCE = new Barium();
    private static final FabricLoader fabricInstance = FabricLoader.getInstance();
    public static List<ModContainer> mods = new LinkedList<>();
    // mods we will not update
    public final Map<String, File> ignoredMods = new HashMap<>();
    private JsonObject bariumProperties;
    //  private final String[] gameVersions = {"1.14", "1.15", "1.16", "1.17"};
    // mods we can update
    private static final Map<String, File> modMap = new HashMap<>();
    public boolean shouldInstall = false;

    @Override
    public void onInitialize() {
        // Run this on closing
        Runtime.getRuntime().addShutdownHook(new Thread(this::install));
    }

    public void install() {
        // When we install, we delete all the mods in modMap, and leave the ones in ignoredMods. Then, we move all the files
        // from mods/temp/ to mods/
        // We do this on closing of the game to not cause memory leaks and/or instability
        
        // First we need to check if we actually want to install
        if (!this.shouldInstall) return;
        
        // now we actually get on to the meat and potatoes
        Arrays.stream(Objects.requireNonNull(fabricInstance.getGameDir().resolve("mods").toFile().listFiles())).forEach(file -> {
            // if the mod is ignored, we skip it over
            if (ignoredMods.containsValue(file)) return;
            // if it is not ignored, we delete it
            file.delete();
        });
    }


    public static Barium getInstance() {
        return INSTANCE;
    }

    static {
        mods.addAll(fabricInstance.getAllMods());
        try(BufferedReader fr = new BufferedReader(new FileReader(fabricInstance.getConfigDir().resolve("barium.json").toFile().getAbsolutePath()))) {
            StringBuilder sb = new StringBuilder();

            fr.lines().forEach(sb::append);

            getInstance().bariumProperties = new Gson().fromJson(sb.toString(), JsonObject.class);
            /*
            We are going to have a JSONObject like
            "ignored": {
                "mod-id": "file-path"
            }
            however, the "file-path" string will have to be updated when we update.
            the modMap will take care of this for us

            Here we load the modMap, with all the mods that we CAN update.
            However, all the mods that are not updated will be in the ignoredMap and therefore will not be removed
             */
            Arrays.stream(Objects.requireNonNull(new File(String.valueOf(fabricInstance.getGameDir().resolve("mods"))).listFiles())).forEach(file -> {
                if(!file.getName().endsWith(".jar")) return;

                try {
                    ZipFile zipFile = new ZipFile(file);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while(entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.getName().equals("fabric.mod.json")) continue;
                        InputStream in = zipFile.getInputStream(entry);
                        // Here we get the ID of the mod we have accessed
                        final StringBuilder sb2 = new StringBuilder();
                        Arrays.stream(new byte[][]{in.readAllBytes()}).forEach(sb2::append);
                        String id = new Gson().fromJson(sb2.toString(), JsonObject.class).get("id").getAsString();
                        // Check if the ID is ignored
                        if (getInstance().bariumProperties.get("ignored").getAsJsonObject().has(id)) {
                            // add it to ignored mods
                            getInstance().ignoredMods.put(id, file);
                            // stop it from adding the mod to modMap
                            return;
                        };
                        // if not ignored, add the mod to the modMap
                        modMap.put(id, file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            getInstance().bariumProperties = null;
        }
    }
}
