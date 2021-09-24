package dev.themyth.barium.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.themyth.barium.Barium;
import dev.themyth.barium.mod_platform.ModFile;
import dev.themyth.barium.mod_platform.ModToDownload;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

public class Downloader {
    public static String sendPost(String hash) throws Exception{
        String body = "[" + hash + "]";

        HttpURLConnection urlConn;
        URL mUrl = new URL("https://addons-ecs.forgesvc.net/api/v2/fingerprint");
        urlConn = (HttpURLConnection) mUrl.openConnection();
        urlConn.setDoOutput(true);

        urlConn.addRequestProperty("Accept", "application/json");
        urlConn.addRequestProperty("Content-Type", "application/json");
        urlConn.addRequestProperty("Content-Type", "application/json");
        urlConn.getOutputStream().write(body.getBytes("UTF8"));

        StringBuilder content;
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

        String line;
        content = new StringBuilder();
        while ((line = br.readLine()) != null) {
            content.append(line);
        }
        urlConn.disconnect();

        if (content.toString().contains("\"exactMatches\":[]")) {
            return null;
        }
        return content.toString();
    }

    public static JsonObject getJsonObject(String url) {
        try {
            return new Gson().fromJson(new InputStreamReader(new URL(url).openStream()), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonArray getJsonArray(String url) {
        try {
            return new Gson().fromJson(new InputStreamReader(new URL(url).openStream()), JsonArray.class);
        } catch (Exception e){
            return null;
        }
    }

    public static void downloadFile(URL url, String filename) {
        // so game dont freze
        new Thread( () -> {
            try {
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(FabricLoader.getInstance().getGameDir().resolve("mods").resolve(filename).toString());
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static ModFile getNewUpdate(JsonArray array, ModToDownload modToDownload, String provider) {
        ModFile modFile = null;
        FileTime newestDate = FileTime.from(Instant.parse(modToDownload.date));

        for (JsonElement i : array) {
            ModFile modRelease = new ModFile(i.getAsJsonObject(), provider);

            if(modRelease.isFabric) {
                if (modRelease.isCompatible(Barium.getMinecraftVersion())) {
                    FileTime fileDate = FileTime.from(Instant.parse(modRelease.fileDate));
                    if (newestDate.compareTo(fileDate) < 0) {
                        newestDate = fileDate;
                        modFile = modRelease;
                    }
                }
            }
        }
        return modFile;
    }
}
