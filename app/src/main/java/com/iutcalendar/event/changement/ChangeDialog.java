package com.iutcalendar.event.changement;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import com.univlyon1.tools.agenda.R;

public class ChangeDialog extends Dialog {

    public ChangeDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.changement_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentHistory);
        Bundle args = new Bundle();
        args.putString(HistoryFragment.DATE_AFFICHAGE, "today");
        fragmentContainerView.getFragment().setArguments(args);
    }
}
