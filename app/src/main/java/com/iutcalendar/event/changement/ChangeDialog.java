package com.iutcalendar.event.changement;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import com.univlyon1.tools.agenda.R;

public class ChangeDialog extends Dialog {

    private final int nombreChangements;

    public ChangeDialog(@NonNull Context context, int nombreChangements) {
        super(context);
        setContentView(R.layout.changement_dialog);

        this.nombreChangements = nombreChangements;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentHistory);
        Bundle args = new Bundle();
        args.putInt(HistoryFragment.NOMBRE_EVENT, nombreChangements);
        fragmentContainerView.getFragment().setArguments(args);
    }
}
