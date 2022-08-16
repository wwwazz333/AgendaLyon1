package com.iutcalendar.data;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.event.ChangeEventListener;
import com.iutcalendar.event.changement.EventChangment;
import com.iutcalendar.event.changement.EventChangmentManager;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.filedownload.WrongURLException;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.snackbar.ErrorSnackBar;
import com.univlyon1.tools.agenda.R;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileGlobal {
    public static final String SAVED_CAL = "savedCal.ics";
    public static final String PERSONAL_TASKS = "personalTasks.dat";
    public static final String PERSONAL_ALARMS = "personalAlarms.dat";
    public static final String PERSONAL_ALARM_CONDITIONS = "personalAlarmConditions.dat";
    public static final String PERSONAL_ALARM_CONSTRAINTS = "personalAlarmConstraints.dat";
    public static final String CHANGEMENT_EVENT = "changementEvent.dat";


    public static String getPathDownladDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public static File getFile(Context context, String whichFile) {
        return new File(getPathDownladDir(context) + "/" + whichFile);
    }

    public static File getFileDownload(Context context) {
        return getFile(context, SAVED_CAL);
    }

    public static File getFilePersonnalTask(Context context) {
        return getFile(context, PERSONAL_TASKS);
    }

    public static File getFilePersonnalAlarm(Context context) {
        return getFile(context, PERSONAL_ALARMS);
    }

    public static File getFileConditions(Context context) {
        return getFile(context, PERSONAL_ALARM_CONDITIONS);
    }

    public static File getFileConstraints(Context context) {
        return getFile(context, PERSONAL_ALARM_CONSTRAINTS);
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
        try {
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

            List<EventChangment> changes = nouveau.getChangedEvent(prev);
            //sauvgarde l'historique des changements
            EventChangmentManager.getInstance(context).getChangmentList().addAll(changes);
            EventChangmentManager.getInstance(context).save(context);

            if (!changes.isEmpty()) {
                Intent intent = new Intent(context, PageEventActivity.class);

                DataGlobal.save(context, DataGlobal.NOMBRE_CHANGE_TO_DISPLAY, changes.size());

                onChangeListener.ifChange(context, intent);
            }
        } catch (UnknownHostException e) {
            if (context instanceof PageEventActivity) {
                PageEventActivity a = (PageEventActivity) context;
                ErrorSnackBar.showError(a.getBinding().getRoot(), a.getString(R.string.No_connexion));
            }
        } catch (WrongURLException e) {
            if (context instanceof PageEventActivity) {
                PageEventActivity a = (PageEventActivity) context;
                ErrorSnackBar.showError(a.getBinding().getRoot(), a.getString(R.string.Wrong_URL));
            }
        } catch (Exception e) {
            if (context instanceof PageEventActivity) {
                PageEventActivity a = (PageEventActivity) context;
                ErrorSnackBar.showError(a.getBinding().getRoot(), a.getString(R.string.Error));
            }
            Log.e("SnackBar", e.getMessage());
        }
    }
}


