package com.iutcalendar.filedownload;

import android.util.Log;
import com.iutcalendar.MainActivity;

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

    public static void updateFichier(String file_path) {
        try { // update du fichier ou création
            ReadableByteChannel readChannel =
                    getCalender("http://adelb.univ-lyon1.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=51554&projectId=1&calType=ical&firstDate=2022-01-02&lastDate=2022-07-03");
            saveByteToFile(readChannel, file_path);
            System.out.println("fichier enregistré");
        } catch (IOException e) {
            System.out.println("IOException erreur update file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur update file : " + e.getMessage());
        }
    }

}
