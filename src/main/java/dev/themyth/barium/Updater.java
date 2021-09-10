package dev.themyth.barium;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.themyth.barium.utils.GithubAPIStructure;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Updater {
    public final char sep = File.separatorChar;
    // None of this is relavant, back to JS :pain:
    public void update() {
        String jarPath;
        for(ModContainer mod : Barium.mods) {
            jarPath = getJarPathFromAPI(mod);
            downloadFile(jarPath);
        }
    }
    // this is still relevant
    public void downloadFile(String url) {
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream(FabricLoader.getInstance().getGameDir().resolve("mods"
                    + sep
                    + "temp"
                    + sep
                    + url.split("/")[url.split("/").length - 1]).toString());
            FileChannel fileChannel = fos.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getJarPathFromAPI(ModContainer mod) {
        ModType type = getModType(mod);
        switch (type) {
            case GITHUB:

                new GithubAPIStructure(getData());
                break;
            case CURSE:
                break;
            case MODRINTH:
                break;
            case CONFUSION:
                break;
        }
        return "";
    }

    public ModType getModType(ModContainer mod) {
        AtomicReference<ModType> type = new AtomicReference<>(ModType.CONFUSION);
        mod.getMetadata().getContact().get("sources").ifPresentOrElse((string) ->{
            if(string.contains("github")) type.set(ModType.GITHUB);
        }, () -> {
            System.err.println("Mod " + mod.getMetadata().getName() + " does not have a sources link, moving to homepage");
        });
        mod.getMetadata().getContact().get("homepage").ifPresentOrElse( (string) -> {
            if(string.contains("github")) type.set(ModType.GITHUB);
            if(string.contains("curseforge")) type.set(ModType.CURSE);
            if(string.contains("modrinth")) type.set(ModType.MODRINTH);
        }, () -> System.err.println("Mod does not have homepage: " + mod.getMetadata().getName()));

        return type.get();
    }

    public void getData(String url) {
        AtomicReference<JsonElement> data1 = null;
        new Thread( () -> {
            String data = "";
            try {
                data = new BufferedReader( new InputStreamReader( new URL(url).openStream())).lines().collect(Collectors.joining());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            data1.set(new Gson().fromJson(data, JsonElement.class));
        });
        data1.get();
    }
}
enum ModType {
    GITHUB,
    MODRINTH,
    CURSE,
    CONFUSION
}
