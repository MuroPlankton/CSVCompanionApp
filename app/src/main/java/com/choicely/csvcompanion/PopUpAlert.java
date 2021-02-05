package com.choicely.csvcompanion;

import android.app.Activity;
import android.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;


public class PopUpAlert {

    public void alertPopUp(Activity activity, int message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> activity.finish())
                .setNegativeButton("No", (dialog, which) -> {
                });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

    public void askForUserValidation(Activity activity, int message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            activity.finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> {

        });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }
}
