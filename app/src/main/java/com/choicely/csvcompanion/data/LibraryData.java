package com.choicely.csvcompanion.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LibraryData extends RealmObject {

    @PrimaryKey
    private int libraryID;
    private String libraryName;
    private RealmList<LanguageData> languages = new RealmList<>();
    private RealmList<TextData> texts = new RealmList<>();

    public int getLibraryID() { return libraryID; }

    public void setLibraryID(int libraryID) { this.libraryID = libraryID; }

    public String getLibraryName() { return libraryName; }

    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }

    public RealmList<LanguageData> getLanguages() {
        return languages;
    }

    public void setLanguages(RealmList<LanguageData> languages) {
        this.languages = languages;
    }

    public RealmList<TextData> getTexts() {
        return texts;
    }

    public void setTexts(RealmList<TextData> texts) {
        this.texts = texts;
    }
}
