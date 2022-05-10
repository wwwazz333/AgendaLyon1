package com.iutcalendar.data;

import android.content.Context;
import android.util.Log;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class DataSaver {
    private final static String url = "url_path";
    private final static String pathFileDownload = "path_file_download";
    private final static String calendrier = "object_calendrier";

    public static void savePath(Context context, String data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(url, data).commit();
    }

    public static String getSavedPath(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(url, "");
    }

    public static void savePathDownloadFile(Context context, String data){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(pathFileDownload, data).commit();
    }
    public static String getSavedPathDownloadFile(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(pathFileDownload, "");
    }

    public static void saveCal(Context context, String data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(calendrier, data).commit();
    }

    public static void saveCal(Context context, ReadableByteChannel data) {
        try {
            saveCal(context, readableByteChannel2String(data));
        } catch (IOException e) {
            Log.e("Save", "save calendar with chanel");
        }
    }

    public static String getSavedCal(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(calendrier, "");
    }

    private static String readableByteChannel2String(ReadableByteChannel channel) throws IOException {
        StringBuilder builder = new StringBuilder();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        while (channel.read(byteBuffer) > 0) {

            //limit is set to current position and position is set to zero
            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                char c = (char) byteBuffer.get();
                builder.append(c);
            }
            byteBuffer = ByteBuffer.allocate(512);
        }

        return builder.toString();
    }

}
