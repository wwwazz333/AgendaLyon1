package com.iutcalendar.snackbar;

import android.content.Context;
import android.view.View;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.univlyon1.tools.agenda.R;

public class ErrorSnackBar {
    public static void showError(View view, CharSequence text) {
        Context context = view.getContext();
        Snackbar snackbar = Snackbar.make(view, text, BaseTransientBottomBar.LENGTH_LONG);


        snackbar.setBackgroundTint(context.getColor(R.color.redOnePlus));
        snackbar.setTextColor(context.getColor(R.color.white));
        snackbar.setActionTextColor(context.getColor(R.color.white));


        snackbar.setAction("╳", v -> snackbar.dismiss());
        snackbar.show();
    }
}
