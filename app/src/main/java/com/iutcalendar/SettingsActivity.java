package com.iutcalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.calendar.iutcalendar.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        findViewById(R.id.btnRetour).setOnClickListener(view -> {
            finish();
        });

        findViewById(R.id.btnScan).setOnClickListener(view -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(SettingsActivity.this);

            intentIntegrator.setPrompt("test");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setCaptureActivity(CaptureActivity.class);

            intentIntegrator.initiateScan();
        });

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(res.getContents() != null){
            Toast.makeText(getApplicationContext(), res.getContents(), Toast.LENGTH_LONG).show();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("url_path", res.getContents()).commit();
        }else{
            Toast.makeText(getApplicationContext(), "res.getContents()", Toast.LENGTH_LONG).show();
        }
    }
}