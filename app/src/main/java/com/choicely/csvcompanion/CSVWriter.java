package com.choicely.csvcompanion;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class CSVWriter {

    private final String TAG = "CSVWriter";
    private int textsUpdated = 0;
    private String libraryID;
    private Context context;
    private List<String> textKeys = new ArrayList<>();

    private FirebaseDBHelper.onDatabaseUpdateListener libraryListener = new FirebaseDBHelper.onDatabaseUpdateListener() {
        @Override
        public void onDatabaseUpdate() {
            FirebaseDBHelper firebaseDBHelper = FirebaseDBHelper.getInstance();
            firebaseDBHelper.setTextLoadListener(textsListener);
            firebaseDBHelper.readAndUpdateSingleText(libraryID, textKeys.get(textsUpdated));
        }
    };

    private FirebaseDBHelper.onSingleTextLoadedListener textsListener = new FirebaseDBHelper.onSingleTextLoadedListener() {
        @Override
        public void onSingleTextLoaded() {
            textsUpdated++;
            if (textsUpdated < textKeys.size()) {
                FirebaseDBHelper.getInstance().readAndUpdateSingleText(libraryID, textKeys.get(textsUpdated));
            } else {
                writeCSVFile();
            }
        }
    };

    public CSVWriter(String libraryKey, Context ctx) {
        libraryID = libraryKey;
        context = ctx;

        List<TextData> texts = RealmHelper.getInstance().getRealm().where(LibraryData.class).equalTo("libraryID", libraryID).findFirst().getTexts();
        for (int index = 0; index < texts.size(); index++) {
            textKeys.add(texts.get(index).getTextKey());
        }

//        FirebaseDBHelper.getInstance().setListener(libraryListener);
        writeCSVFile();
    }

    private void writeCSVFile() {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();
        Log.d(TAG, "writeCSVFile: LibraryData: " + libraryData);

        File csvFile = new File(context.getExternalFilesDir(null), libraryData.getLibraryName() + ".csv");

        List<String> columns = new ArrayList<>();
        columns.add("android,");
        columns.add("ios,");
        columns.add("web,");

        List<LanguageData> languageDataList = libraryData.getLanguages();
        for (int index = 0; index < languageDataList.size() - 1; index++) {
            Log.d(TAG, "Single language to be written: " + languageDataList.get(index));
            columns.add(languageDataList.get(index).getLangKey() + ",");
        }
        columns.add(languageDataList.get(languageDataList.size() - 1).getLangKey());

        String line = "";
        for (String columnName : columns) {
            line += columnName;
        }
        String output = line + "\n";
        line = "";

        List<TextData> textDataList = libraryData.getTexts();
        for (TextData textData : textDataList) {
            Log.d(TAG, "Text to be written: " + textData);
            line += (textData.getAndroidKey() == null || textData.getAndroidKey() == "") ? "," : textData.getAndroidKey() + ",";
            line += (textData.getIosKey() == null || textData.getIosKey() == "") ? "," : textData.getIosKey() + ",";
            line += (textData.getWebKey() == null || textData.getWebKey() == "") ? "," : textData.getWebKey() + ",";

            Map<String, String> translations = new HashMap<>();
            List<SingleTranslationData> singleTranslationDataList = textData.getTranslations();
            for (SingleTranslationData singleTranslationData : singleTranslationDataList) {
                translations.put(singleTranslationData.getLangKey(), singleTranslationData.getTranslation());
            }
            for (int i = 3; i < columns.size() - 1; i++) {
                line += (translations.containsKey(columns.get(i))) ? translations.get(columns.get(i)) + "," : ",";
            }
            line += translations.containsKey(columns.get(columns.size() - 1)) ? translations.get(columns.get(columns.size() - 1)) : "";

            output += line + "\n";
            line = "";
        }

        try {
            OutputStream outputStream = new FileOutputStream(csvFile);
            outputStream.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
