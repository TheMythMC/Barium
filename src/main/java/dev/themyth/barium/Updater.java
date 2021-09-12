package dev.themyth.barium;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.themyth.barium.utils.Downloader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Updater {
    public static Updater INSTANCE = new Updater();
    public boolean didDownload = false;

    public void update() {
        // Called on actual update

        // String is URL to download
        List<String> modsToDownload = getModsToDownload();
        downloadList(modsToDownload);
    }
    public List<String> getModsToDownload() {
        List<String> temp = new ArrayList<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://themyth.dev/api/barium/mods-list");
        List<NameValuePair> params = new ArrayList<>();
        Barium.modMap.forEach( (k, v) -> params.add(new BasicNameValuePair(k, "get")));
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity == null) throw new IOException();
            InputStream in = entity.getContent();
            new Gson().fromJson(readAll(in), JsonObject.class).getAsJsonObject().entrySet().forEach((entry) -> temp.add(entry.getValue().getAsString()));
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readAll(InputStream reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int codePoint;
        while ((codePoint = reader.read()) != -1) {
            stringBuilder.append((char) codePoint);
        }
        return stringBuilder.toString();
    }

    public void downloadList(List<String> strings) {
        if (strings == null) {
            didDownload = false;
            return;
        }
        strings.forEach(string -> {
            Downloader.downloadFile(string, Paths.get(string.split("/")[string.split("/").length - 1]));
        });
        didDownload = true;
    }

}