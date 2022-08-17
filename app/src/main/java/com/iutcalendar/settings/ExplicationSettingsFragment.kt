package com.iutcalendar.settings

import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.iutcalendar.data.DataGlobal
import com.univlyon1.tools.agenda.R

class ExplicationSettingsFragment : Fragment() {
    private var countClick = 0
    private var elapsedTimeLastClick: Long = 0
    private var lastTimeClick: Long = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explication_settings, container, false)
        val version = view.findViewById<TextView>(R.id.versionLabel)
        val pm = context!!.packageManager
        val pkgName = context!!.packageName
        val pkgInfo: PackageInfo?
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0)
            version.text = getString(R.string.version_build, pkgInfo.versionName)
        } catch (e: NameNotFoundException) {
            Log.e("Information", "error version build affichage : " + e.message)
        }
        version.setOnClickListener { view1: View? ->
            if (lastTimeClick == 0L) {
                elapsedTimeLastClick = 0
                lastTimeClick = System.currentTimeMillis()
            }
            elapsedTimeLastClick = System.currentTimeMillis() - lastTimeClick
            if (countClick < 5) {
                if (elapsedTimeLastClick < 300L) {
                    countClick++
                    lastTimeClick = System.currentTimeMillis()
                } else {
                    elapsedTimeLastClick = 0
                    countClick = 0
                    lastTimeClick = 0
                }
            } else {
                elapsedTimeLastClick = 0
                countClick = 0
                lastTimeClick = 0
                DataGlobal.setDebugging(context, !DataGlobal.isDebug(context))
                if (DataGlobal.isDebug(context)) {
                    Toast.makeText(context, "Activation Debugging", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Désactivation Debugging", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }
}