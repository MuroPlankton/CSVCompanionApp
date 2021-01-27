package com.choicely.csvcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        Handler handler = new Handler();
        handler.postDelayed(this::moveToMainScreen, 5000);
    }

    private void moveToMainScreen() {
        Intent intent = new Intent(this, FirebaseUIActivity.class);
        startActivity(intent);

        finish();
    }
}
