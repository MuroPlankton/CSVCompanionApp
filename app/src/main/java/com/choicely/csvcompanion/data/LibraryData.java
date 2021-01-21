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
    private List<String> langKeys = new ArrayList<>();
    private List<String> langValues = new ArrayList<>();
    private List<String> translationKeys = new ArrayList<>();
    private List<TranslationData> translationData = new ArrayList<>();

    public int getLibraryID() { return libraryID; }

    public void setLibraryID(int libraryID) { this.libraryID = libraryID; }

    public String getLibraryName() { return libraryName; }

    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }

    public List<String> getLangKeys() {
        return langKeys;
    }

    public List<String> getLangValues() {
        return langValues;
    }

    public void setLanguages(List<String> keys, List<String> values) {
        this.langKeys = keys;
        this.langValues = values;
    }

    public List<String> getTranslationKeys() {
        return translationKeys;
    }

    public List<TranslationData> getTranslationData() {
        return translationData;
    }

    public void setTexts(List<String> keys, List<TranslationData> values) {
        this.translationKeys = keys;
        this.translationData = values;
    }

    public TranslationData findTranslationByID(String translationID) {
        return translationData.get(translationKeys.indexOf(translationID));
    }
}
