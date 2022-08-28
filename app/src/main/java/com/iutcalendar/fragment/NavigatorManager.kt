package com.iutcalendar.fragment

import android.view.ViewGroup
import androidx.fragment.app.Fragment

object NavigatorManager {
    fun startFragmentFromFragment(currentFragment: Fragment, fragmentToStart: Fragment, tag: String? = null) {
        currentFragment.requireActivity().supportFragmentManager.beginTransaction()
            .replace((currentFragment.view?.parent as ViewGroup).id, fragmentToStart, tag)
            .addToBackStack(null)
            .commit()
    }
}