package com.iutcalendar.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.menu.MenuItemClickActivities
import com.univlyon1.tools.agenda.R

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private var actionBar: ActionBar? = null
    fun increaseCountArborescenceFragment() {
        countArborescenceFragment++
        updateActionBar()
    }

    private fun decreaseCountArborescenceFragment() {
        countArborescenceFragment--
        updateActionBar()
    }

    fun comeBackToMainPageSettings() {
        countArborescenceFragment = 0
        updateActionBar()
    }

    private val isMainRoot: Boolean
        get() = countArborescenceFragment == 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_IUTCalendarActionBar)
        SettingsApp.setLocale(resources, DataGlobal.getLanguage(applicationContext))
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        actionBar = supportActionBar
        comeBackToMainPageSettings()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean { // pour la redirection vers les sous menu
        if (pref.fragment != null) {
            increaseCountArborescenceFragment()
            updateActionBar()
            val args = pref.extras
            val fragment = supportFragmentManager.fragmentFactory.instantiate(
                    classLoader,
                    pref.fragment!!)
            fragment.arguments = args
            // Replace the existing Fragment with the new Fragment
            supportFragmentManager.beginTransaction()
                    .replace(R.id.settings, fragment)
                    .addToBackStack(null)
                    .commit()
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MenuBar", "activity")
        val id = item.itemId
        if (id == android.R.id.home) {
            supportFragmentManager.popBackStack()
            decreaseCountArborescenceFragment()
        } else {
            MenuItemClickActivities(this).onMenuItemClick(item)
        }
        updateActionBar()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_activities, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateActionBar() {
        if (actionBar != null) {
            actionBar!!.title = getString(R.string.Settings)
            actionBar!!.setDisplayHomeAsUpEnabled(!isMainRoot)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("Back", countArborescenceFragment.toString())
        if (isMainRoot) {
            val intent = Intent(this, PageEventActivity::class.java)
            finish()
            startActivity(intent)
        } else {
            decreaseCountArborescenceFragment()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var switchComplex: SwitchPreference? = null
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            setToggleComplexAlarm()
            if (activity != null) {
                findPreference<Preference>("alarme_enable")!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    if (!Settings.canDrawOverlays(context) && newValue as Boolean) {
                        DialogMessage.showInfo(activity, "Overlays", context!!.getString(R.string.msg_activ_overlay)) {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity!!.packageName))
                            startActivity(intent)
                        }
                    }
                    Settings.canDrawOverlays(context)
                }
                if (activity is SettingsActivity) {
                    val settingsActivity = activity as SettingsActivity?
                    //Switch fragment
                    settingsActivity?.let { setOnClickFragment(it) }
                }
            }
        }

        private fun setToggleComplexAlarm() {
            switchComplex = findPreference(DataGlobal.COMPLEX_ALARM_SETTINGS)
            if (switchComplex != null) {
                setAlarmComplexity(switchComplex!!.isChecked)
                switchComplex!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    setAlarmComplexity(newValue as Boolean)
                    true
                }
            }
            val themeSelectedPref = findPreference<Preference>(DataGlobal.THEME)
            if (themeSelectedPref != null) {
                themeSelectedPref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    SettingsApp.adapteTheme(newValue.toString())
                    reloadActivity()
                    true
                }
            }
            val languageSelectedPref = findPreference<Preference>(DataGlobal.LANGUAGE)
            if (languageSelectedPref != null) {
                languageSelectedPref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    DataGlobal.save(context, DataGlobal.LANGUAGE, newValue.toString())
                    reloadActivity()
                    true
                }
            }
        }

        private fun setOnClickFragment(settingsActivity: SettingsActivity) {
            findPreference<Preference>("ContactFragment")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                settingsActivity.increaseCountArborescenceFragment()
                settingsActivity.updateActionBar()
                switchFragment(settingsActivity, ContactFragment())
                false
            }
            findPreference<Preference>("ContactFragment")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                settingsActivity.increaseCountArborescenceFragment()
                settingsActivity.updateActionBar()
                switchFragment(settingsActivity, ContactFragment())
                false
            }
            findPreference<Preference>("ExplicationSettingsFragment")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                settingsActivity.increaseCountArborescenceFragment()
                settingsActivity.updateActionBar()
                switchFragment(settingsActivity, ExplicationSettingsFragment())
                false
            }
            findPreference<Preference>("contrainte_alarmes")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                settingsActivity.increaseCountArborescenceFragment()
                settingsActivity.updateActionBar()
                switchFragment(settingsActivity, AlarmConstraintFragment())
                false
            }
            findPreference<Preference>("horaire_alarmes")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                settingsActivity.increaseCountArborescenceFragment()
                settingsActivity.updateActionBar()
                switchFragment(settingsActivity, AlarmConditionFragment())
                false
            }
            findPreference<Preference>("liste_alarmes")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                settingsActivity.increaseCountArborescenceFragment()
                settingsActivity.updateActionBar()
                switchFragment(settingsActivity, AlarmListFragment())
                false
            }
            findPreference<Preference>("URLSetterFragment")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                settingsActivity.increaseCountArborescenceFragment()
                settingsActivity.updateActionBar()
                switchFragment(settingsActivity, URLSetterFragment())
                false
            }
        }

        private fun switchFragment(activity: SettingsActivity, fragment: Fragment) {
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.settings, fragment)
                    .addToBackStack(null)
                    .commit()
        }

        private fun setAlarmComplexity(complex: Boolean) {
            findPreference<Preference>("horaire_alarmes")!!.isEnabled = complex
            findPreference<Preference>("contrainte_alarmes")!!.isEnabled = complex
            findPreference<Preference>("horaire_alarmes")!!.isVisible = complex
            findPreference<Preference>("contrainte_alarmes")!!.isVisible = complex
            findPreference<Preference>("time_before_ring")!!.isEnabled = !complex
            findPreference<Preference>("time_before_ring")!!.isVisible = !complex
            findPreference<Preference>("activated_days")!!.isEnabled = !complex
            findPreference<Preference>("activated_days")!!.isVisible = !complex
        }

        private fun reloadActivity() {
            if (activity != null) {
                activity!!.finish()
                activity!!.overridePendingTransition(0, 0)
                startActivity(activity!!.intent)
                activity!!.overridePendingTransition(0, 0)
            }
        }
    }

    companion object {
        private var countArborescenceFragment = 0
    }
}