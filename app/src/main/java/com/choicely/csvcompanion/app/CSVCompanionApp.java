package com.choicely.csvcompanion.app;

import android.app.Application;
import android.util.Log;

import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;

public class CSVCompanionApp extends Application {

    private static final String TAG = "CSVCompanionApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App start");

        RealmHelper.init(this);
        FirebaseDBHelper.init();
    }
}
