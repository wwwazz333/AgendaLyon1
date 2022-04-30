package com.iutcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceManager;
import com.calendar.iutcalendar.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class URLSetterFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_u_r_l_setter, container, false);

        view.findViewById(R.id.scanBtn).setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());

            intentIntegrator.setPrompt("test");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setCaptureActivity(CaptureActivity.class);

            intentIntegrator.initiateScan();
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(res.getContents() != null){
            Toast.makeText(getContext(), res.getContents(), Toast.LENGTH_LONG).show();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("url_path", res.getContents()).commit();
            EditText input = (EditText) view.findViewById(R.id.inputURL);
            input.setText(res.getContents());
        }else{
            Toast.makeText(getContext(), "res.getContents()", Toast.LENGTH_LONG).show();
        }
    }
}