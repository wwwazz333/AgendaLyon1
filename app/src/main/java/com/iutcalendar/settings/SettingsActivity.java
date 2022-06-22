package com.iutcalendar.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import com.calendar.iutcalendar.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.menu.MenuItemClickActivities;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    //TODO modifier theme

    private static int countArboressenceFragment = 0;

    public static void comeBackToMainPageSettings() {
        countArboressenceFragment = 0;
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


        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {// pour la redirection vers les sous menu
        if (pref.getFragment() != null) {
            countArboressenceFragment++;
            final Bundle args = pref.getExtras();
            final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                    getClassLoader(),
                    pref.getFragment());
            fragment.setArguments(args);
//            fragment.setTargetFragment(caller, 0);//depreciet
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        SwitchPreference switchComplex;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            switchComplex = findPreference(DataGlobal.COMPLEX_ALARM_SETTINGS);
            if (switchComplex != null) {
                setAlarmComplexity(switchComplex.isChecked());
            }
            switchComplex.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    setAlarmComplexity((boolean) newValue);
                    return true;
                }
            });
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (countArboressenceFragment > 0) {
                    getSupportFragmentManager().popBackStack();
                    countArboressenceFragment--;
                } else {
                    finish();
                }
                break;
            default:
                return new MenuItemClickActivities(this).onMenuItemClick(item);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu_activities, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

