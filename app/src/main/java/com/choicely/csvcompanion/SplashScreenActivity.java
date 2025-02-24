package com.choicely.csvcompanion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.main.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class SplashScreenActivity extends Activity {
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "SplashScreenActivity";
    private static final String ROOT_USER_ELEMENT = "users";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadUserLibrariesBeforeStart();
        } else {
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.EmailBuilder().build());

            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                            .build(), 123);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userID = user.getUid();
            String userName = user.getDisplayName();
            FirebaseDatabase.getInstance().getReference().child(ROOT_USER_ELEMENT).child(userID).setValue(userName);

            loadUserLibrariesBeforeStart();
        }
    }

    private void loadUserLibrariesBeforeStart() {
        FirebaseDBHelper.getInstance().setListener(() -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });
        FirebaseDBHelper.getInstance().listenForUserLibraryDataChange();
    }
}
