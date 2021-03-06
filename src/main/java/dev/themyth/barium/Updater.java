package dev.themyth.barium;

import com.google.gson.JsonArray;
import dev.themyth.barium.config.BariumConfig;
import dev.themyth.barium.mod_platform.ModFile;
import dev.themyth.barium.mod_platform.ModToDownload;
import dev.themyth.barium.util.Downloader;
import dev.themyth.barium.util.Hash;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.util.tuples.Triplet;

import java.io.File;
import java.net.URL;
import java.util.*;

public class Updater {
    static boolean fetchRunning = false;

    // depending on what we run, we will implement the install method
    public static List<Triplet<String, URL, File>> fetchAllMods(PlayerEntity player) {
        if (fetchRunning) return null;
        fetchRunning = true;
        List<Triplet<String, URL, File>> temp = new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(FabricLoader.getInstance().getGameDir().resolve("mods").toFile().listFiles())).forEach(file -> {
            if(Arrays.stream(BariumConfig.ignoredMods).toList().contains(file.getName())) return;
            // Barium.sendMessage("Checking " + file.getName().replace(".jar", "") + "..", player);
            Logger LOGGER = LogManager.getLogger();
            LOGGER.info("Checking " + file.getName().replace(".jar", "") + "..", player);
            Triplet<String, URL, File> updated = checkForUpdate(file);
            // check if its null, we dont want an array with nulls
            // nullpointer exceptions are annoying
            if (updated != null) temp.add(updated);
        });
        fetchRunning = false;
        return temp;
    }
    public static Triplet<String, URL, File> checkForUpdate(File modFile) {
        if (modFile.getName().endsWith(".jar")) {
            ModFile newestFile = null;
            try {
                String sha1 = Hash.getSHA1(modFile);
                ModToDownload modToDownload = new ModToDownload(sha1, "modrinth");
                if (modToDownload.name != null) {
                    JsonArray jsonArray = Downloader.getJsonArray("https://api.modrinth.com/api/v1/mod/" + modToDownload.id + "/version");
                    newestFile = Downloader.getNewUpdate(jsonArray, modToDownload, "modrinth");
                    return new Triplet<>(newestFile.fileName, new URL(newestFile.downloadUrl), modFile);
                } else {
                    String murmurHash = Hash.getMurmurHash(modFile);
                    String posted = Downloader.sendPost(murmurHash);

                    if (posted != null) {
                        modToDownload = new ModToDownload(posted, "curseforge");
                        if (modToDownload.name != null) {
                            JsonArray array = Downloader.getJsonArray("https://addons-ecs.forgesvc.net/api/v2/addon/" + modToDownload.id + "/files");
                            newestFile = Downloader.getNewUpdate(array, modToDownload, "curseforge");
                            return new Triplet<>(newestFile.fileName, new URL(newestFile.downloadUrl), modFile);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
