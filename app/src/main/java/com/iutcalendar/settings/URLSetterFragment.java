package com.iutcalendar.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;
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

        valide.setOnClickListener(v -> {
            FileGlobal.getFileDownload(getContext()).delete();
//            DataSaver.saveCal(getContext(), "");
            DataGlobal.savePath(getContext(), input.getText().toString());
            FileDownload.updateFichier(FileGlobal.getFileDownload(getContext()).getAbsolutePath(), getContext());
            getParentFragmentManager().popBackStackImmediate();


        });
        cancel.setOnClickListener(v -> getParentFragmentManager().popBackStackImmediate());

        return view;
    }
}