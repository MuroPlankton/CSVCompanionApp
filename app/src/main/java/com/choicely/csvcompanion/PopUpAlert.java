package com.choicely.csvcompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class PopUpAlert {

    private static final String TAG = "PopUpAlert";

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

            AuthUI.getInstance()
                    .signOut(activity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: signed out");
                        }
                    });
        });
        builder.setNegativeButton("No", (dialog, which) -> {

        });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }
}
