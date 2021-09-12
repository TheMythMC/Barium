package dev.themyth.barium.utils;

import com.google.gson.JsonObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

public class Downloader {
    public static JsonObject downloadJsonFile()
    {

    }

    public static boolean downloadFile(String url, Path path) {
        boolean didFail = false;
        try {
            URL mod = new URL(url);
            ReadableByteChannel readableByteChannel = Channels.newChannel(mod.openStream());
            String[] urlArray = url.split("/");
            FileOutputStream fos = new FileOutputStream(urlArray[urlArray.length - 1]);
            fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            didFail = true;
            e.printStackTrace();

        }
        return !didFail;
    }

}
