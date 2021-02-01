package com.choicely.csvcompanion;

import android.os.Environment;

import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVWriter {

    public static void writeCSVFile(LibraryData libraryData) {
        new Thread(() -> {
//            Realm realm = Realm.getDefaultInstance();
//            LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryKey).findFirst();

            File root = Environment.getDataDirectory();
            File csvFile = new File(root, libraryData.getLibraryName() + ".csv");

            List<String> columns = new ArrayList<>();
            columns.add("android");
            columns.add("ios");
            columns.add("web");

            List<LanguageData> languageDataList = libraryData.getLanguages();
            for (LanguageData languageData : languageDataList) {
                columns.add(languageData.getLangKey());
            }

            try {
                FileWriter writer = new FileWriter(csvFile);

                String line = null;
                for (String columnName : columns) {
                    line += columnName;
                }
                writer.write(line);
                line = "";

                List<TextData> textDataList = libraryData.getTexts();
                for (TextData textData : textDataList) {
                    line += (textData.getAndroidKey().isEmpty()) ? "," : textData.getAndroidKey();
                    line += (textData.getIosKey().isEmpty()) ? "," : textData.getIosKey();
                    line += (textData.getWebKey().isEmpty()) ? "," : textData.getWebKey();

                    Map<String, String> translations = new HashMap<>();
                    List<SingleTranslationData> singleTranslationDataList = textData.getTranslations();
                    for (SingleTranslationData singleTranslationData : singleTranslationDataList) {
                        translations.put(singleTranslationData.getLangKey(), singleTranslationData.getTranslation());
                    }
                    for (int i = 3; i < columns.size(); i++) {
                        line += (translations.containsKey(columns.get(i))) ? translations.get(columns.get(i)) : ",";
                    }

                    writer.write(line);
                    line = "";
                }

                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            realm.close();
        }).start();
    }
}
