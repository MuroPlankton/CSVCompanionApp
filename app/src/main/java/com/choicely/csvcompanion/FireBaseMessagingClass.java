package com.choicely.csvcompanion;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FireBaseMessagingClass extends FirebaseMessagingService {


    private static final String TAG = "FirebaseMessagingClass";

    public void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            String token = task.getResult();
            Log.d(TAG, "token: " + token);
        });
    }

    public void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("outer space")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String message = "subscribe was successful";
                        if (!task.isSuccessful()) {
                            message = "subscribe failed";
                        }
                        Log.d(TAG, "Message: " + message);
                    }
                });
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "New token: " + s);
    }
}
