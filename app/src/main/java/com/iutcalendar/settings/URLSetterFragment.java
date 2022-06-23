package com.iutcalendar.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.univlyon1.tools.agenda.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.filedownload.FileDownload;
import com.journeyapps.barcodescanner.CaptureActivity;

public class URLSetterFragment extends Fragment {

    View view;
    Button scan, valide, cancel;
    EditText input;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_url_setter, container, false);

        scan = view.findViewById(R.id.scanBtn);
        valide = view.findViewById(R.id.submitBtn);
        cancel = view.findViewById(R.id.cancelBtn);
        input = view.findViewById(R.id.inputURL);

        input.setText(DataGlobal.getSavedPath(getContext()));

        scan.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setCaptureActivity(CaptureActivity.class);

            intentIntegrator.initiateScan();
        });

        final String prevURL = input.getText().toString();

        SettingsActivity parentActivity = (SettingsActivity) getActivity();
        valide.setOnClickListener(v -> {
            if (!prevURL.equals(input.getText().toString())) {
                FileGlobal.getFileDownload(getContext()).delete();
                DataGlobal.savePath(getContext(), input.getText().toString());

                new Thread(() -> FileDownload.updateFichier(FileGlobal.getFileDownload(getContext()).getAbsolutePath(), getContext())).start();

            }
            parentActivity.comeBackToMainPageSettings();
            getParentFragmentManager().popBackStackImmediate();
        });
        cancel.setOnClickListener(v -> {
            parentActivity.comeBackToMainPageSettings();
            getParentFragmentManager().popBackStackImmediate();
        });

        return view;
    }
}