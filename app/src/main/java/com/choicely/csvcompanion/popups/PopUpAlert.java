package com.choicely.csvcompanion.popups;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.choicely.csvcompanion.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class PopUpAlert {

    private static final String TAG = "PopUpAlert";

    public void alertPopUp(Activity activity, int message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setNegativeButton(R.string.continue_editing_popup_text, (dialog, which) -> { })
                .setPositiveButton(R.string.dont_save_popup_text, (dialog, which) -> activity.finish());

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
