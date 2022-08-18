package com.iutcalendar.menu

import android.view.MenuItem
import androidx.fragment.app.Fragment

abstract class AbstractFragmentWitheMenu : Fragment() {
    abstract fun clickMenu(item: MenuItem)
}