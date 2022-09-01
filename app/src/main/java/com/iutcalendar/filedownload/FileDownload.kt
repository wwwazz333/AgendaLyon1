package com.iutcalendar.filedownload

import android.content.Context
import android.util.Log
import com.iutcalendar.alarm.Alarm
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.InputStreamFileException
import com.iutcalendar.data.CachedData
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

object FileDownload {
    private const val DEFAULT_BUFFER_SIZE = 8192

    enum class URLValidity {
        VALIDE, INVALIDE, ERROR_CONNECTION
    }

    fun isValideURL(urlCalender: String): URLValidity {
        return try {
            val url = URL(urlCalender)
            val huc = url.openConnection() as HttpURLConnection
            huc.instanceFollowRedirects = false
            huc.connect()
            huc.disconnect()
            URLValidity.VALIDE
        } catch (e: UnknownHostException) {
            URLValidity.ERROR_CONNECTION
        } catch (e: Exception) {
            Log.e("URL", "$e")
            URLValidity.INVALIDE
        }
    }

    @Throws(IOException::class)
    fun getCalendar(urlCalender: String): InputStream? {
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
    fun updateFichierCalendrier(context: Context, url_path: String? = null): Boolean {
        // update du fichier ou création
        val url = url_path ?: DataGlobal.getSavedPath(context) ?: ""
        return updateFichier(FileGlobal.getFileCalendar(context).absolutePath, url).also { success ->
            if (success) {
                Log.d("File", "fichier enregistré : ${FileGlobal.getFileCalendar(context).exists()}")
                CachedData.calendrier = Calendrier(FileGlobal.readFile(FileGlobal.getFileCalendar(context)))
                Log.d("File", "cached data calendrier has ${CachedData.calendrier.events.size}")
                Alarm.setUpAlarm(context, CachedData.calendrier) //met a jours les alarmes programmé
            }
        }
    }

    @Throws(IOException::class, InputStreamFileException::class)
    fun updateFichier(file_path: String?, url_path: String): Boolean {
        // update du fichier ou création
        try {
            return updateFileWithURL(file_path, url_path)
        } catch (e: FileNotFoundException) {
            throw WrongURLException()
        }
    }

    @Throws(Exception::class)
    private fun updateFileWithURL(file_path: String?, url_path: String): Boolean {
        return Calendrier.writeCalendarFile(downloadURL(url_path), File(file_path.toString()))
    }

    @Throws(IOException::class, InputStreamFileException::class)
    fun downloadRoomsCalendar(context: Context, url_path: String? = null): Calendrier {
        try {
            return if (url_path != null) downloadCalendar(url_path) else
                DataGlobal.getSavedRoomsPath(context)?.let { url ->
                    downloadCalendar(url)
                } ?: Calendrier(mutableListOf())
        } catch (e: FileNotFoundException) {
            throw WrongURLException()
        }

    }

    @Throws(Exception::class)
    private fun downloadCalendar(url_path: String): Calendrier {
        return Calendrier(downloadURL(url_path))
    }

    @Throws(Exception::class)
    private fun downloadURL(url_path: String): String {
        if (url_path.isEmpty() || isValideURL(url_path) == URLValidity.INVALIDE) {
            throw WrongURLException()
        }
        val inputStream = getCalendar(url_path) ?: throw InputStreamFileException("input stream est null")

        return inputStream2String(inputStream)
    }


    @Throws(IOException::class)
    private fun inputStream2String(inputStream: InputStream): String {
        val result = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var length: Int
        while (inputStream.read(buffer).also { length = it } != -1) {
            result.write(buffer, 0, length)
        }
        return result.toString("UTF-8")
    }
}