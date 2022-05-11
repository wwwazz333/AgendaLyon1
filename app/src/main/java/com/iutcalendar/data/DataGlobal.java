package com.iutcalendar.data;

import android.content.Context;
import androidx.preference.PreferenceManager;

public class DataGlobal {
    private final static String url = "url_path";
    private final static String pathFileDownload = "path_file_download";
//    private final static String calendrier = "object_calendrier";

    //String
    public static void save(Context context, String key, String data){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, data).commit();
    }
    public static String getSavedString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }
    //boolean
    public static void save(Context context, String key, boolean data){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, data).commit();
    }
    public static boolean getSavedBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }
    //boolean
    public static void save(Context context, String key, int data){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, data).commit();
    }
    public static int getSavedInt(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }


    public static void savePath(Context context, String data) {
        save(context, url, data);
    }

    public static String getSavedPath(Context context) {
        return getSavedString(context, url);
    }

    public static void savePathDownloadFile(Context context, String data) {
        save(context, pathFileDownload, data);
    }

    public static String getSavedPathDownloadFile(Context context) {
        return getSavedString(context, pathFileDownload);
    }


    /*
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
     */

}
