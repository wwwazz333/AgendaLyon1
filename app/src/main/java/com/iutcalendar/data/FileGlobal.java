package com.iutcalendar.data;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.event.ChangeEventListener;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.mainpage.PageEventActivity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileGlobal {
    public static final String nameFileSave = "savedCal.ics";
    public static final String nameFilePersonnalTask = "personalTasks.dat";
    public static final String nameFilePersonnalAlarm = "personalAlarms.dat";
    public static final String nameFileConditionAlarm = "personalAlarmConditions.dat";
    public static final String nameFileConstraintAlarm = "personalAlarmConstraints.dat";


    public static String getPathDownladDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public static File getFileDownload(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFileSave);
    }

    public static File getFilePersonnalTask(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFilePersonnalTask);
    }

    public static File getFilePersonnalAlarm(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFilePersonnalAlarm);
    }

    public static File getFileConditions(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFileConditionAlarm);
    }

    public static File getFileConstraints(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFileConstraintAlarm);
    }


    public static String readFile(File file) {
        StringBuilder build = new StringBuilder();

        Path path = Paths.get(file.getAbsolutePath());

        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(path);
        } catch (FileNotFoundException | NoSuchFileException e) {
            return "";
        } catch (IOException e) {
            Log.e("File", e.getMessage());
            return "";
        }
        try {
            String str;
            while ((str = reader.readLine()) != null) {
                build.append(str).append('\n');
            }
        } catch (IOException e) {
            Log.e("File", e.getMessage());
            return "";
        }

        return build.toString();
    }

    public static boolean writeFile(String contentToWrite, File fileToWrite) throws IOException {
        File temporaryFile = new File(fileToWrite.getParent() + "/~" + fileToWrite.getName());
        Path p = Paths.get(temporaryFile.getAbsolutePath());

        BufferedWriter buf = Files.newBufferedWriter(p);
        buf.write(contentToWrite);
        buf.close();

        if (temporaryFile.renameTo(fileToWrite)) {
            return true;
        } else {
            Log.e("File", "error rename temporary file");
            return false;
        }
    }

    public static boolean writeBinaryFile(Object objectToWrite, File fileToWrite) {
        File temporaryFile = new File(fileToWrite.getParent() + "/~" + fileToWrite.getName());

        FileOutputStream stream;
        try {
            stream = new FileOutputStream(temporaryFile);
        } catch (FileNotFoundException e) {
            Log.w("File", "binary file doesn't existe " + e.getMessage());
            return false;
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(objectToWrite);
            out.close();
            stream.close();
            Log.d("File", "file " + fileToWrite.getName() + " saved");
        } catch (IOException e) {
            Log.e("File", "couldn't write in file " + fileToWrite.getName());
        }

        if (temporaryFile.renameTo(fileToWrite)) {
            return true;
        } else {
            Log.e("File", "error rename temporary file : " + fileToWrite.getName());
            return false;
        }
    }

    public static <T> T loadBinaryFile(File fileToRead) {
        FileInputStream stream;
        T readingObject = null;
        try {
            stream = new FileInputStream(fileToRead);
        } catch (FileNotFoundException e) {
            Log.w("File", fileToRead.getName() + " doesn't existe.");
            return null;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(stream);
            Object obj = in.readObject();
            if (obj != null) {
                readingObject = (T) obj;
            } else {
                Log.e("File", fileToRead.getName() + " error : wrong type, please delete " + fileToRead.getName());
            }

            Log.d("File", fileToRead.getName() + " loaded");
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("File", "class non trouvé pour " + fileToRead.getName() + " : " + e.getMessage());
        }
        return readingObject;
    }

    public static void updateAndGetChange(Context context, Calendrier calendrier, ChangeEventListener onChangeListener) {
        Calendrier prev;
        if (calendrier != null) {
            prev = calendrier.clone();
        } else {
            prev = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(context)));
        }
        FileDownload.updateFichier(FileGlobal.getFileDownload(context).getAbsolutePath(), context);

        Calendrier nouveau;
        if (calendrier != null) {
            nouveau = calendrier;
            nouveau.loadFromString(FileGlobal.readFile(FileGlobal.getFileDownload(context)));
        } else {
            nouveau = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(context)));
        }

        nouveau.deleteUselessTask(context);

        List<Tuple<EventCalendrier, Calendrier.InfoChange>> changes = nouveau.getChangedEvent(prev);
        if (!changes.isEmpty()) {
            String changesMsg = Calendrier.changeToString(context, changes);

            //TODO string: message de notif
            Intent intent = new Intent(context, PageEventActivity.class);
            intent.putExtra("changes", changesMsg);

            onChangeListener.ifChange(context, intent);
        }
    }
}


