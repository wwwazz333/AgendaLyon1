package com.iutcalendar.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;
import com.iutcalendar.data.DataGlobal;

public class ExplicationSettingsFragment extends Fragment {

    private int countClick = 0;
    private long eleapsedTimeLastClick = 0, lastTimeClick = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explication_settings, container, false);

        TextView version = view.findViewById(R.id.versionLabel);

        PackageManager pm = getContext().getPackageManager();
        String pkgName = getContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);

            version.setText(getString(R.string.version_build, pkgInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Information", "error version build affichage : " + e.getMessage());
        }

        version.setOnClickListener(view1 -> {

            if (lastTimeClick == 0L) {
                eleapsedTimeLastClick = 0;
                lastTimeClick = System.currentTimeMillis();
            }
            eleapsedTimeLastClick = System.currentTimeMillis() - lastTimeClick;
            if (countClick < 5) {
                if (eleapsedTimeLastClick < 300L) {
                    countClick++;
                    lastTimeClick = System.currentTimeMillis();
                } else {
                    eleapsedTimeLastClick = 0;
                    countClick = 0;
                    lastTimeClick = 0;
                }
            } else {
                eleapsedTimeLastClick = 0;
                countClick = 0;
                lastTimeClick = 0;
                DataGlobal.setDebugings(getContext(), !DataGlobal.isDebug(getContext()));

                if (DataGlobal.isDebug(getContext())) {
                    Toast.makeText(getContext(), "Activation Debuging", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Désactivation Debuging", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }
}