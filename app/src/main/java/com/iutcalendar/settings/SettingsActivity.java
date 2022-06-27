package com.iutcalendar.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.dialog.DialogMessage;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.menu.MenuItemClickActivities;
import com.univlyon1.tools.agenda.R;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    //TODO possiblité désactivé notif changement event
    private static int countArboressenceFragment = 0;
    private ActionBar actionBar;

    public void incressCountArboressenceFragment() {
        countArboressenceFragment++;
        updateActionBar();
    }

    public void decressCountArboressenceFragment() {
        countArboressenceFragment--;
        updateActionBar();
    }

    public void comeBackToMainPageSettings() {
        countArboressenceFragment = 0;
        updateActionBar();
    }

    private boolean isMainRoot() {
        return countArboressenceFragment == 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_IUTCalendarActionBar);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));
        setContentView(R.layout.settings_activity);


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }


        actionBar = getSupportActionBar();
        comeBackToMainPageSettings();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {// pour la redirection vers les sous menu
        if (pref.getFragment() != null) {
            incressCountArboressenceFragment();
            updateActionBar();
            final Bundle args = pref.getExtras();
            final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                    getClassLoader(),
                    pref.getFragment());
            fragment.setArguments(args);
            // Replace the existing Fragment with the new Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            decressCountArboressenceFragment();
        } else {
            return new MenuItemClickActivities(this).onMenuItemClick(item);
        }
        updateActionBar();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activities, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateActionBar() {
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.Settings));
            actionBar.setDisplayHomeAsUpEnabled(!isMainRoot());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("Back", String.valueOf(countArboressenceFragment));
        if (isMainRoot()) {
            Intent intent = new Intent(this, PageEventActivity.class);
            finish();
            startActivity(intent);
        } else {
            decressCountArboressenceFragment();
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        SwitchPreference switchComplex;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            setToggleComplexAlarm();

            if (getActivity() != null) {
                findPreference("alarme_enable").setOnPreferenceChangeListener((preference, newValue) -> {
                    if (!Settings.canDrawOverlays(getContext()) && (boolean) newValue) {
                        DialogMessage.showInfo(getActivity(), "Overlays", getContext().getString(R.string.msg_activ_overlay), () -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                            startActivity(intent);
                        });
                    }
                    return Settings.canDrawOverlays(getContext());
                });

                if (getActivity() instanceof SettingsActivity) {
                    SettingsActivity settingsActivity = (SettingsActivity) getActivity();
                    //Switch fragment
                    if (settingsActivity != null) {
                        setOnClickFragment(settingsActivity);
                    }
                }
            }
        }

        private void setToggleComplexAlarm() {
            switchComplex = findPreference(DataGlobal.COMPLEX_ALARM_SETTINGS);
            if (switchComplex != null) {
                setAlarmComplexity(switchComplex.isChecked());

                switchComplex.setOnPreferenceChangeListener((preference, newValue) -> {
                    setAlarmComplexity((boolean) newValue);
                    return true;
                });
            }

            Preference themeSelectedPref = findPreference(DataGlobal.THEME);
            if (themeSelectedPref != null) {
                themeSelectedPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    SettingsApp.adapteTheme(String.valueOf(newValue));

                    reloadActivity();
                    return true;
                });
            }

            Preference languageSelectedPref = findPreference(DataGlobal.LANGUAGUE);
            if (languageSelectedPref != null) {
                languageSelectedPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    DataGlobal.save(getContext(), DataGlobal.LANGUAGUE, String.valueOf(newValue));

                    reloadActivity();
                    return true;
                });
            }
        }

        private void setOnClickFragment(SettingsActivity settingsActivity) {
            findPreference("ContactFragment").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    settingsActivity.incressCountArboressenceFragment();
                    settingsActivity.updateActionBar();
                    switchFragment(settingsActivity, new ContactFragment());
                    return false;
                }
            });

            findPreference("ContactFragment").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    settingsActivity.incressCountArboressenceFragment();
                    settingsActivity.updateActionBar();
                    switchFragment(settingsActivity, new ContactFragment());
                    return false;
                }
            });

            findPreference("ExplicationSettingsFragment").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    settingsActivity.incressCountArboressenceFragment();
                    settingsActivity.updateActionBar();
                    switchFragment(settingsActivity, new ExplicationSettingsFragment());
                    return false;
                }
            });

            findPreference("contrainte_alarmes").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    settingsActivity.incressCountArboressenceFragment();
                    settingsActivity.updateActionBar();
                    switchFragment(settingsActivity, new AlarmConstraintFragment());
                    return false;
                }
            });

            findPreference("horaire_alarmes").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    settingsActivity.incressCountArboressenceFragment();
                    settingsActivity.updateActionBar();
                    switchFragment(settingsActivity, new AlarmConditionFragment());
                    return false;
                }
            });

            findPreference("liste_alarmes").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    settingsActivity.incressCountArboressenceFragment();
                    settingsActivity.updateActionBar();
                    switchFragment(settingsActivity, new AlarmListFragment());
                    return false;
                }
            });

            findPreference("URLSetterFragment").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    settingsActivity.incressCountArboressenceFragment();
                    settingsActivity.updateActionBar();
                    switchFragment(settingsActivity, new URLSetterFragment());
                    return false;
                }
            });
        }

        private void switchFragment(SettingsActivity activity, Fragment fragment) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        private void setAlarmComplexity(boolean complex) {
            findPreference("horaire_alarmes").setEnabled(complex);
            findPreference("contrainte_alarmes").setEnabled(complex);
            findPreference("horaire_alarmes").setVisible(complex);
            findPreference("contrainte_alarmes").setVisible(complex);

            findPreference("time_before_ring").setEnabled(!complex);
            findPreference("time_before_ring").setVisible(!complex);
            findPreference("activated_days").setEnabled(!complex);
            findPreference("activated_days").setVisible(!complex);
        }

        private void reloadActivity() {
            if (getActivity() != null) {
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(getActivity().getIntent());
                getActivity().overridePendingTransition(0, 0);
            }
        }
    }
}

