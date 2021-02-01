package com.choicely.csvcompanion.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LibraryData extends RealmObject {

    @PrimaryKey
    private String libraryID;
    private String libraryName;
    private Object user;
    private RealmList<LanguageData> languages = new RealmList<>();
    private RealmList<TextData> texts = new RealmList<>();

    public String getLibraryID() {
        return libraryID;
    }

    public void setLibraryID(String libraryID) {
        this.libraryID = libraryID;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

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
