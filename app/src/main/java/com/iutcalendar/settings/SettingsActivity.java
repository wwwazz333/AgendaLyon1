package com.iutcalendar.settings;

import android.content.Intent;
import android.os.Bundle;
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
import com.univlyon1.tools.agenda.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.menu.MenuItemClickActivities;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (res.getContents() != null) {
            Toast.makeText(getApplicationContext(), "QR code scané", Toast.LENGTH_SHORT).show();
            EditText input = findViewById(R.id.inputURL);
            input.setText(res.getContents());
        } else {
            Toast.makeText(getApplicationContext(), "échec", Toast.LENGTH_SHORT).show();
        }
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
        }else{
            decressCountArboressenceFragment();
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        SwitchPreference switchComplex;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
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

