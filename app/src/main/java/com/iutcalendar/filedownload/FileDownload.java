package com.iutcalendar.filedownload;

import android.content.Context;
import androidx.preference.PreferenceManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class FileDownload {

    public static ReadableByteChannel getCalender(String urlCalender) throws IOException {
        URL url = new URL(urlCalender);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setInstanceFollowRedirects(false);
        huc.connect();
        String newLocationHeader = null;
        while ((huc.getResponseCode() / 100) == 3) {
            newLocationHeader = huc.getHeaderField("Location");
            huc.disconnect();
            url = new URL(newLocationHeader);
            huc = (HttpURLConnection) url.openConnection();
            huc.setInstanceFollowRedirects(false);
            huc.connect();
        }
        huc.disconnect();
        if (newLocationHeader != null) {
            return Channels.newChannel(new URL(newLocationHeader).openStream());
        }
        return null;
    }

    public static void saveByteToFile(ReadableByteChannel readChannel, String path) throws IOException {

        FileOutputStream fileOS = new FileOutputStream(path);
        FileChannel writeChannel = fileOS.getChannel();

        writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);

        fileOS.close();
    }

    public static void updateFichier(String file_path, Context context) {
        try { // update du fichier ou création
            String url = PreferenceManager.getDefaultSharedPreferences(context).getString("url_path", "");
            if (!url.isEmpty()) {
                ReadableByteChannel readChannel =
                        getCalender(url);
                saveByteToFile(readChannel, file_path);
                System.out.println("fichier enregistré");
            }
        } catch (IOException e) {
            System.out.println("IOException erreur update file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur update file : " + e.getMessage());
        }
    }

}
