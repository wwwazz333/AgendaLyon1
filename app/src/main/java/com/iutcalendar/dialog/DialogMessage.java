package com.iutcalendar.dialog;

import android.app.AlertDialog;
import android.content.Context;
import com.calendar.iutcalendar.R;

public class DialogMessage {

    public static void showWarning(Context context, String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_error);

        alertDialog.setPositiveButton(context.getString(R.string.ok),
                (dialog, which) -> {
                    dialog.dismiss();
                });

        alertDialog.show();
    }

    public static void showInfo(Context context, String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_info);

        alertDialog.setPositiveButton(context.getString(R.string.ok),
                (dialog, which) -> {
                    dialog.dismiss();
                });

        alertDialog.show();
    }

    public static void showAide(Context context, String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_help);

        alertDialog.setPositiveButton(context.getString(R.string.ok),
                (dialog, which) -> {
                    dialog.dismiss();
                });

        alertDialog.show();
    }
}
