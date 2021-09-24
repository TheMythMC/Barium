package dev.themyth.barium.mod_platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.themyth.barium.util.Downloader;

import java.util.Objects;

public class ModToDownload {
    public String id;
    public String date;
    public String mod;
    public String url;
    public String name;
    public ModToDownload(String hashOrResult, String hoster) {
        if (Objects.equals(hoster, "curseforge")) {
            JsonParser jp = new JsonParser();
            JsonObject json = jp.parse(hashOrResult).getAsJsonObject();
            id = json.get("exactMatches").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
            date = json.get("exactMatches").getAsJsonArray().get(0).getAsJsonObject().get("file").getAsJsonObject().get("fileDate").getAsString();
            name = json.get("exactMatches").getAsJsonArray().get(0).getAsJsonObject().get("file").getAsJsonObject().get("fileName").getAsString();

            json = Downloader.getJsonObject("https://addons-ecs.forgesvc.net/api/v2/addon/" + id);
            assert json != null;
            mod = json.get("name").getAsString();
            url = json.get("websiteUrl").getAsString() + "/files";
        }
        else if (Objects.equals(hoster, "modrinth")) {
            JsonObject json = Downloader.getJsonObject("https://api.modrinth.com/api/v1/version_file/" + hashOrResult + "?algorithm=sha1");
            assert json != null;
            id = json.get("mod_id").getAsString();
            date = json.get("date_published").getAsString();
            final JsonArray filesArray = json.getAsJsonArray("files");

            filesArray.forEach(file -> {
                String temp = file.getAsJsonObject().get("filename").getAsString();
                if(!temp.contains("-sources") && !temp.contains("-dev")) {
                    this.name = file.getAsJsonObject().get("filename").getAsString();
                }
            });

            json = Downloader.getJsonObject("https://api.modrinth.com/api/v1/mod" + id);
            assert json != null;
            mod = json.get("title").getAsString();
            url = "https://modrinth.com/mod/" + json.get("slug").getAsString() + "/versions";
        }
        assert mod != null;
        mod = mod.replace("(fabric)", "");
        mod = mod.replace("(Fabric)", "");

        while (Character.toString(mod.charAt(mod.length()-1)).equals(" ")) {
            mod = mod.substring(0, mod.length() - 1);
        }
    }
}
