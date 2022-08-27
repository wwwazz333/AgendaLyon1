package com.iutcalendar.filedownload

import android.content.Context
import android.util.Log
import com.iutcalendar.alarm.Alarm
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.InputStreamFileException
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object FileDownload {
    private const val DEFAULT_BUFFER_SIZE = 8192

    @Throws(IOException::class)
    fun getCalender(urlCalender: String?): InputStream? {
        Log.d("File", "Downloading file")
        var url = URL(urlCalender)
        var huc = url.openConnection() as HttpURLConnection
        huc.instanceFollowRedirects = false
        huc.connect()
        var newLocationHeader: String? = null
        while (huc.responseCode / 100 == 3) {
            newLocationHeader = huc.getHeaderField("Location")
            huc.disconnect()
            url = URL(newLocationHeader)
            huc = url.openConnection() as HttpURLConnection
            huc.instanceFollowRedirects = false
            huc.connect()
        }
        huc.disconnect()
        return if (newLocationHeader != null) {
            URL(newLocationHeader).openStream()
        } else null
    }

    @Throws(IOException::class, InputStreamFileException::class)
    fun updateFichier(file_path: String?, context: Context?): Boolean {

        // update du fichier ou création
        DataGlobal.getSavedPath(context)?.let { url ->
            if (url.isEmpty()) {
                throw WrongURLException()
            }
            val inputStream = getCalender(url) ?: throw InputStreamFileException("input stream est null")

            val contentFile = inputStream2String(inputStream)
            val success = Calendrier.writeCalendarFile(contentFile, File(file_path.toString()))
            if (success) {
                Log.d("File", "fichier enregistré")
                val calendrier = Calendrier(FileGlobal.readFile(FileGlobal.getFileCalendar(context)))
                Alarm.setUpAlarm(context, calendrier)
            }
            return success
        }
        return false
    }

    @Throws(IOException::class)
    private fun inputStream2String(`is`: InputStream): String {
        val result = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var length: Int
        while (`is`.read(buffer).also { length = it } != -1) {
            result.write(buffer, 0, length)
        }
        return result.toString("UTF-8")
    }
}