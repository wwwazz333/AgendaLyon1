package com.iutcalendar.filedownload;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.InputStreamFileException;
import com.iutcalendar.calendrier.InvalideFormatException;
import com.iutcalendar.data.DataGlobal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class FileDownload {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static InputStream getCalender(String urlCalender) throws IOException {
        Log.d("File", "Downloading file");
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
            return new URL(newLocationHeader).openStream();
        }
        return null;
    }

    public static boolean updateFichier(String file_path, Context context) throws IOException, InputStreamFileException {
        boolean succes = false;

//        try { // update du fichier ou création
            String url = DataGlobal.getSavedPath(context);
            if (url.isEmpty()) {
                throw new WrongURLException();
            }
            InputStream inputStream = getCalender(url);
            if (inputStream == null) {
                throw new InputStreamFileException("input stream est null");
            }
            String conentFile = inputStream2String(inputStream);

            succes = Calendrier.writeCalendarFile(conentFile, new File(file_path));
            if (succes) Log.d("File", "fichier enregistré");
//        } catch (InvalideFormatException e) {
//            Log.e("File", "format file : " + e.getMessage());
//        } catch (InputStreamFileException e) {
//            Log.e("File", "input stream : " + e.getMessage());
//        } catch (IOException e) {
//            Log.e("File", "IOException erreur update file: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e("File", "Erreur update file : " + e.getMessage());
//            e.printStackTrace();
//        }

        return succes;
    }

    private static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }

}
