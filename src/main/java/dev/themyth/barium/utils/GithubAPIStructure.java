package dev.themyth.barium.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

public class GithubAPIStructure {
    public final List<Asset> assets = new LinkedList<>();
    public GithubAPIStructure(JsonObject data) {
        data.get("assets").getAsJsonArray().forEach(asset -> assets.add(new Asset(asset.getAsJsonObject())));
    }
    public static class Asset{
        public final String downloadUrl;
        public Asset(JsonObject data) {
            downloadUrl = data.get("browser_download_url").getAsString();
        }
    }
}
