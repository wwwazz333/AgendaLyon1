package com.iutcalendar.menu

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class AbstractFragmentWitheMenu : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
}