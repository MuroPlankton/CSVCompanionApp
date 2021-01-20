package com.choicely.csvcompanion.app;

import android.app.Application;

import com.choicely.csvcompanion.db.RealmHelper;

public class CSVCompanionApp extends Application {

    private static final String TAG = "CSVCompanionApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        RealmHelper.init(this);
    }
}
