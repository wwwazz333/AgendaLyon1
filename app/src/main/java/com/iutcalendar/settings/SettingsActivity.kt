package com.iutcalendar.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.menu.AbstractFragmentWitheMenu
import com.iutcalendar.menu.MenuItemClickActivities
import com.univlyon1.tools.agenda.R

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private var actionBar: ActionBar? = null
    private val getFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.settings)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsApp.setLocale(resources, DataGlobal.getLanguage(applicationContext))
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        actionBar = supportActionBar
        actionBar?.title = getString(R.string.Settings)

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                Log.d("ActionBar", "onCreateMenu for $getFragment")

                when (getFragment) {
                    is SettingsFragment -> {
                        menuInflater.inflate(R.menu.menu_activities, menu)
                    }
                    is AlarmConditionFragment,
                    is AlarmConstraintFragment -> {
                        menuInflater.inflate(R.menu.menu_action_help_and_add_alarm, menu)
                    }
                    is AlarmListFragment -> {
                        menuInflater.inflate(R.menu.menu_action_settings_alarm, menu)
                    }
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val id = menuItem.itemId
                if (id == android.R.id.home) {
                    if (getFragment is SettingsFragment) goBackToCalendar()
                    else supportFragmentManager.popBackStackImmediate()
                } else {
                    MenuItemClickActivities(this@SettingsActivity).onMenuItemClick(menuItem)
                }
                if (getFragment is AbstractFragmentWitheMenu) {
                    (getFragment as AbstractFragmentWitheMenu).clickMenu(menuItem)
                }
                return true
            }

        })
    }

    fun goBackToCalendar() {
        val intent = Intent(this@SettingsActivity, PageEventActivity::class.java)
        finish()
        startActivity(intent)
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean { // pour la redirection vers les sous menu
        pref.fragment?.let {
            val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, it)
                .apply { arguments = pref.extras }
            // Replace the existing Fragment with the new Fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit()
            return true
        }
        return false
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var askedToDrawOverLays = false
        override fun onResume() {
            super.onResume()
            if (askedToDrawOverLays && Settings.canDrawOverlays(requireContext())) {
                DataGlobal.save(requireContext(), "alarme_enable", true)
                reloadActivity()
            }
            askedToDrawOverLays = false
        }


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            initAlarmActivation()
            findPreference<Preference>("sub_prefence_alarm")?.fragment = SettingsAlarmFragment::class.java.name


            findPreference<Preference>(DataGlobal.THEME)?.let {
                it.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                        SettingsApp.adapteTheme(newValue.toString())
                        true
                    }
            }
            findPreference<Preference>(DataGlobal.LANGUAGE)?.let {
                it.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                        DataGlobal.save(requireContext(), DataGlobal.LANGUAGE, newValue.toString())
                        reloadActivity()
                        true
                    }
            }


            if (requireActivity() is SettingsActivity) {
                val settingsActivity = requireActivity() as SettingsActivity?
                //Switch fragment
                settingsActivity?.let { setOnClickFragment() }
            }
        }

        private fun initAlarmActivation() {

            findPreference<Preference>("alarme_enable")?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    if (!Settings.canDrawOverlays(requireContext()) && newValue as Boolean) {
                        DialogMessage.showInfo(
                            requireActivity(),
                            "Overlays",
                            requireContext().getString(R.string.msg_activ_overlay)
                        ) {
                            askedToDrawOverLays = true
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + requireActivity().packageName)
                            )
                            startActivity(intent)
                        }
                    }
                    return@OnPreferenceChangeListener Settings.canDrawOverlays(requireContext()) || !(newValue as Boolean)
                }
        }


        private fun setOnClickFragment() {
            findPreference<Preference>("ContactFragment")?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    switchFragment(ContactFragment())
                    false
                }
            findPreference<Preference>("ExplicationSettingsFragment")?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    switchFragment(ExplicationSettingsFragment())
                    false
                }
            findPreference<Preference>("URLSetterFragment")?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    switchFragment(URLSetterFragment())
                    false
                }
        }

        private fun switchFragment(fragment: Fragment) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit()
        }

        private fun reloadActivity() {
            requireActivity().let {
                it.finish()
                it.overridePendingTransition(0, 0)
                startActivity(requireActivity().intent)
                it.overridePendingTransition(0, 0)
            }
        }
    }
}
