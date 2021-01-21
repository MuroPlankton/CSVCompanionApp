package com.choicely.csvcompanion.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LibraryData extends RealmObject {

    @PrimaryKey
    private int libraryID;
    private String libraryName;
    private Map<String, String> languages = new HashMap<>();
    private Map<String, TranslationData> texts = new HashMap<>();

    public int getLibraryID() { return libraryID; }

    public void setLibraryID(int libraryID) { this.libraryID = libraryID; }

    public String getLibraryName() { return libraryName; }

    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }

    public Map<String, String> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<String, String> languages) {
        this.languages = languages;
    }

    public Map<String, TranslationData> getTexts() {
        return texts;
    }

    public void setTexts(Map<String, TranslationData> texts) {
        this.texts = texts;
    }

    public TranslationData findTranslationByID(String translationID) {
        return texts.get(translationID);
    }
}
