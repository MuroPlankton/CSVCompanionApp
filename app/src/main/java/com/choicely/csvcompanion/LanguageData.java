package com.choicely.csvcompanion;

import java.util.UUID;

import io.realm.RealmObject;

public class LanguageData extends RealmObject {

    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


}
