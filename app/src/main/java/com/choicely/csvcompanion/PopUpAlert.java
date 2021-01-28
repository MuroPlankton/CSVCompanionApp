package com.choicely.csvcompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class PopUpAlert {

    public void alertPopUp(Activity activity, int message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

}
