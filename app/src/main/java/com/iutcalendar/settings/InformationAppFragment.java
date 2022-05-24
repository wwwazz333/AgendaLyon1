package com.iutcalendar.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;

public class InformationAppFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information_app, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

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
        return view;
    }
}