package com.iutcalendar.settings

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.iutcalendar.alarm.Alarm
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.math.MyMath
import com.univlyon1.tools.agenda.R

class SettingsAlarmFragment : PreferenceFragmentCompat() {
    private var switchComplex: SwitchPreference? = null

    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult? ->
            activityResult?.let { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    Log.d("Ringtone", "chosen ringtone : ${result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)}")
                    DataGlobal.save(
                        requireContext(),
                        Alarm.RINGTONE_ALARM,
                        result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI).toString()
                    )
                }
            }
        }
    private val intentRingtonePicker
        get() = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.choose_ringtone))
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                Alarm.getUriRingtone(requireContext())
            )//requireContext() need to be attached that is why the getter
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.alarm_preferences, rootKey)

        initSeekBar()
        setToggleComplexAlarm()



        findPreference<Preference>("ringtone_picker")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivityForResult.launch(intentRingtonePicker)
            true
        }

        if (requireActivity() is SettingsActivity) {
            val settingsActivity = requireActivity() as SettingsActivity?
            //Switch fragment
            settingsActivity?.let { setOnClickFragment() }
        }

    }

    private fun initSeekBar() {
        findPreference<SeekBarPreference>("time_before_ring")?.let {
            it.onPreferenceChangeListener = object : SeekBar.OnSeekBarChangeListener,
                Preference.OnPreferenceChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onPreferenceChange(
                    preference: Preference,
                    newValue: Any?
                ): Boolean {
                    it.value = MyMath.roundAt(newValue as Int)
                    return false
                }


            }
        }
    }

    private fun setOnClickFragment() {
        findPreference<Preference>("contrainte_alarmes")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                switchFragment(AlarmConstraintFragment())
                false
            }
        findPreference<Preference>("horaire_alarmes")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                switchFragment(AlarmConditionFragment())
                false
            }
        findPreference<Preference>("liste_alarmes")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                switchFragment(AlarmListFragment())
                false
            }
    }

    private fun setToggleComplexAlarm() {
        switchComplex = findPreference(DataGlobal.COMPLEX_ALARM_SETTINGS)
        if (switchComplex != null) {
            switchComplex?.isChecked?.let { setAlarmComplexity(it) }
            switchComplex!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    setAlarmComplexity(newValue as Boolean)
                    true
                }
        }
    }

    private fun setAlarmComplexity(complex: Boolean) {
        complex.let {
            findPreference<Preference>("horaire_alarmes")?.isEnabled = it
            findPreference<Preference>("contrainte_alarmes")?.isEnabled = it
            findPreference<Preference>("horaire_alarmes")?.isVisible = it
            findPreference<Preference>("contrainte_alarmes")?.isVisible = it
            findPreference<Preference>("time_before_ring")?.isEnabled = !it
            findPreference<Preference>("time_before_ring")?.isVisible = !it
            findPreference<Preference>("activated_days")?.isEnabled = !it
            findPreference<Preference>("activated_days")?.isVisible = !it
        }
    }

    private fun switchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
    }
}