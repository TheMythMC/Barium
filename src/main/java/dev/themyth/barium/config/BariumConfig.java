package dev.themyth.barium.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class BariumConfig {
   public static JsonObject properties;
   public static List<String> ignoredMods = new ArrayList<>();

    public BariumConfig() {
        try {
            List<String> temp = new ArrayList<>();
            properties = new JsonParser().parse(new BufferedReader(new FileReader(FabricLoader.getInstance().getConfigDir().resolve("barium-properties.json").toFile()))).getAsJsonObject();
            properties.getAsJsonArray("ignoredMods").forEach(mod -> {
                temp.add(mod.getAsString());
            });
            ignoredMods = temp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
