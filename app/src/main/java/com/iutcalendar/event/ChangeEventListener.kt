package com.iutcalendar.event

import android.content.Context
import android.content.Intent

interface ChangeEventListener {
    fun ifChange(context: Context?, intent: Intent?)
}