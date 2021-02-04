package com.choicely.csvcompanion;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;
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

    private static final String TAG = "CSVWriter";

    public static void writeCSVFile(String libraryID, Context context) {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        File csvfile = new File(context.getExternalFilesDir(null), libraryData.getLibraryName() + ".csv");
        Log.d(TAG, csvfile.toString());
        String output = "";

        List<String> columns = new ArrayList<>();
        columns.add("android,");
        columns.add("ios,");
        columns.add("web,");

        List<LanguageData> languageDataList = libraryData.getLanguages();
        for (int index = 0; index < languageDataList.size() - 1; index++) {
            columns.add(languageDataList.get(index).getLangKey() + ",");
        }
        columns.add(languageDataList.get(languageDataList.size() - 1).getLangKey());

        String line = null;
        for (String columnName : columns) {
            line += columnName;
        }
        output += line + "\n";
        line = "";

        List<TextData> textDataList = libraryData.getTexts();
        for (TextData textData : textDataList) {
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
            OutputStream outputStream = new FileOutputStream(csvfile);
            outputStream.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
