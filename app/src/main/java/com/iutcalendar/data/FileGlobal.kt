package com.iutcalendar.data

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.event.changement.EventChangementManager
import com.iutcalendar.filedownload.FileDownload
import com.iutcalendar.filedownload.WrongURLException
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.snackbar.ErrorSnackBar
import com.univlyon1.tools.agenda.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.UnknownHostException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

object FileGlobal {

    private const val SAVED_CAL = "savedCal.ics"
    private const val SAVED_CAL_ROOMS = "savedCalRooms.ics"
    private const val PERSONAL_TASKS = "personalTasks.dat"
    private const val PERSONAL_ALARMS = "personalAlarms.dat"
    private const val PERSONAL_ALARM_CONDITIONS = "personalAlarmConditions.dat"
    private const val PERSONAL_ALARM_CONSTRAINTS = "personalAlarmConstraints.dat"
    const val CHANGEMENT_EVENT = "changementEvent.dat"
    private fun getPathDownloadDir(context: Context?): String {
        return context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath
    }

    fun getFile(context: Context?, whichFile: String?): File {
        return File(getPathDownloadDir(context) + "/" + whichFile)
    }

    fun getFileCalendar(context: Context?): File {
        return getFile(context, SAVED_CAL)
    }

    fun getFileCalRooms(context: Context?): File {
        return getFile(context, SAVED_CAL_ROOMS)
    }

    fun getFilePersonalTask(context: Context?): File {
        return getFile(context, PERSONAL_TASKS)
    }

    fun getFilePersonalAlarm(context: Context?): File {
        return getFile(context, PERSONAL_ALARMS)
    }

    fun getFileConditions(context: Context?): File {
        return getFile(context, PERSONAL_ALARM_CONDITIONS)
    }

    fun getFileConstraints(context: Context?): File {
        return getFile(context, PERSONAL_ALARM_CONSTRAINTS)
    }

    fun readFile(file: File?): String {
        val build = StringBuilder()
        val path = Paths.get(file!!.absolutePath)
        val reader: BufferedReader = try {
            Files.newBufferedReader(path)
        } catch (e: FileNotFoundException) {
            return ""
        } catch (e: NoSuchFileException) {
            return ""
        } catch (e: IOException) {
            Log.e("File", e.message!!)
            return ""
        }
        try {
            var str: String?
            while (reader.readLine().also { str = it } != null) {
                build.append(str).append('\n')
            }
        } catch (e: IOException) {
            Log.e("File", e.message!!)
            return ""
        }
        return build.toString()
    }

    @Throws(IOException::class)
    fun writeFile(contentToWrite: String?, fileToWrite: File): Boolean {
        val temporaryFile = File(fileToWrite.parent!! + "/~" + fileToWrite.name)
        val p = Paths.get(temporaryFile.absolutePath)
        val buf = Files.newBufferedWriter(p)
        buf.write(contentToWrite)
        buf.close()
        return if (temporaryFile.renameTo(fileToWrite)) {
            true
        } else {
            Log.e("File", "error rename temporary file")
            false
        }
    }

    fun writeBinaryFile(objectToWrite: Any?, fileToWrite: File?): Boolean {
        val temporaryFile = File(fileToWrite!!.parent!! + "/~" + fileToWrite.name)
        val stream: FileOutputStream = try {
            FileOutputStream(temporaryFile)
        } catch (e: FileNotFoundException) {
            Log.w("File", "binary file doesn't existe " + e.message)
            return false
        }
        try {
            val out = ObjectOutputStream(stream)
            out.writeObject(objectToWrite)
            out.close()
            stream.close()
            Log.d("File", "file " + fileToWrite.name + " saved")
        } catch (e: IOException) {
            Log.e("File", "couldn't write in file " + fileToWrite.name)
        }
        return if (temporaryFile.renameTo(fileToWrite)) {
            true
        } else {
            Log.e("File", "error rename temporary file : " + fileToWrite.name)
            false
        }
    }

    fun <T> loadBinaryFile(fileToRead: File?): T? {
        var readingObject: T? = null
        val stream: FileInputStream = try {
            FileInputStream(fileToRead)
        } catch (e: FileNotFoundException) {
            Log.w("File", fileToRead!!.name + " doesn't existe.")
            return null
        }
        try {
            val `in` = ObjectInputStream(stream)
            val obj = `in`.readObject()
            if (obj != null) {
                readingObject = obj as T
            } else {
                Log.e("File", fileToRead!!.name + " error : wrong type, please delete " + fileToRead.name)
            }
            Log.d("File", fileToRead!!.name + " loaded")
        } catch (e: IOException) {
            Log.e("File", e.message!!)
        } catch (e: ClassNotFoundException) {
            Log.e("File", "class non trouvé pour " + fileToRead!!.name + " : " + e.message)
        }
        return readingObject
    }

    fun updateAndGetChange(context: Context?, calendrier: Calendrier?, onChangeListener: (Context?, Intent?) -> Unit) {
        try {
            val prev: Calendrier = calendrier?.clone() ?: Calendrier(readFile(getFileCalendar(context)))
            FileDownload.updateFichier(getFileCalendar(context).absolutePath, context)
            val nouveau: Calendrier
            if (calendrier == null) {
                nouveau = Calendrier(readFile(getFileCalendar(context)))
            } else {
                nouveau = calendrier
                nouveau.loadFromString(readFile(getFileCalendar(context)))
            }
            nouveau.deleteUselessTask(context)
            val changes = nouveau.getChangedEvent(prev)

            EventChangementManager.getInstance(context).apply { //sauvegarde l'historique des changements
                changementList.addAll(changes)
                save(context)
            }
            if (changes.isNotEmpty()) {
                val intent = Intent(context, PageEventActivity::class.java)
                DataGlobal.save(context, DataGlobal.NOMBRE_CHANGE_TO_DISPLAY, changes.size)
                onChangeListener(context, intent)
            }
        } catch (e: UnknownHostException) {
            if (context is PageEventActivity) {
                ErrorSnackBar.showError(context.binding.root, context.getString(R.string.No_connexion))
            }
        } catch (e: WrongURLException) {
            if (context is PageEventActivity) {
                ErrorSnackBar.showError(context.binding.root, context.getString(R.string.Wrong_URL))
            }
        } catch (e: Exception) {
            if (context is PageEventActivity) {
                ErrorSnackBar.showError(context.binding.root, context.getString(R.string.Error))
            }
            Log.e("SnackBar", "${e.cause} -> ${e.message}")
        }
    }
}