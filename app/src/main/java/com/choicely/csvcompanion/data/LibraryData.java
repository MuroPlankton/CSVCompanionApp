package com.choicely.csvcompanion.data;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LibraryData extends RealmObject {

    @PrimaryKey
    private int libraryID;
    private String libraryName;
    private List<String> languages = new ArrayList<>();
    private List<TranslationData> texts = new ArrayList<>();

    public List<TranslationData> getTexts() {
        return texts;
    }

    public void setTexts(List<TranslationData> texts) {
        this.texts = texts;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public int getLibraryID() { return libraryID; }

    public void setLibraryID(int libraryID) { this.libraryID = libraryID; }

    public String getLibraryName() { return libraryName; }

    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }
}
