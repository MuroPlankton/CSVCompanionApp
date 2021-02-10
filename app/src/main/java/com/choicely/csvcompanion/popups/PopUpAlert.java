package com.choicely.csvcompanion.popups;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.choicely.csvcompanion.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class PopUpAlert extends DialogFragment {

    private static final String TAG = "PopUpAlert";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String libraryID;

    public void alertPopUp(Activity activity, int message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setNegativeButton(R.string.continue_editing_popup_text, (dialog, which) -> {
                })
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
                    .addOnCompleteListener(task -> Log.d(TAG, "onComplete: signed out"));
        });
        builder.setNegativeButton("No", (dialog, which) -> {

        });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

    public void shareLibraryPopUp(Activity activity, int message, String title, String userId, String libraryName, String libraryID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);

        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("Optional message");
        builder.setView(input);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            String customMessage = input.getText().toString();
            shareLibraryToUser(userId, customMessage, libraryName, libraryID);
        });
        builder.setNegativeButton("No", (dialog, which) -> {

        });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

    private void shareLibraryToUser(String userID, String customMessage, String libraryName, String libraryID) {
        DatabaseReference myRef = ref.child("user_inbox/" + userID);

        Map<String, Object> sharedLibrary = new HashMap<>();
        Map<String, Object> sharedLibraryContent = new HashMap<>();

        sharedLibraryContent.put("library_name", libraryName);
        sharedLibraryContent.put("custom_message", customMessage);
        sharedLibraryContent.put("sender_name", user.getDisplayName());

        sharedLibrary.put(libraryID, sharedLibraryContent);

        myRef.updateChildren(sharedLibrary);
    }
}
