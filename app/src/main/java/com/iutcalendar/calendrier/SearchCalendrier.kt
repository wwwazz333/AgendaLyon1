package com.iutcalendar.calendrier

import android.content.Context
import android.util.Log
import com.iutcalendar.filedownload.FileDownload

object SearchCalendrier {
    private var isLoaded = false
    private var calendrier: Calendrier = Calendrier(mutableListOf())

    private var allRooms: List<String> = listOf()

    fun loadCalendrierRoom(context: Context) {

        calendrier = FileDownload.downloadRoomsCalendar(context)
        isLoaded = true
    }

    /**
     * rooms which are never use can't be detected
     */
    private fun searchAllRooms() {
        if (isLoaded) {
            val rooms = mutableListOf<String>()

            calendrier.events.forEach { event ->
                event.salle.split("\\,").forEach { salle ->
                    if (!rooms.contains(salle)) {
                        rooms.add(salle)
                    }
                }
            }
            allRooms = rooms.toList()
        }
    }

    fun freeRoomAt(dateCalendrier: DateCalendrier): List<String> {
        if (isLoaded) {
            if (allRooms.isEmpty()) searchAllRooms()
            Log.d("SearchRoom", "toute les salles : $allRooms")
            val roomAvailable = allRooms.toMutableList()
            calendrier.getEventsDuring(dateCalendrier).forEach { event ->
                event.salle.split("\\,").forEach { salle ->
                    roomAvailable.remove(salle)
                }
            }
            return roomAvailable
        }
        return listOf()
    }

}


