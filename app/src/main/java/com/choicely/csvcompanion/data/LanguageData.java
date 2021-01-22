package com.choicely.csvcompanion.data;

import io.realm.RealmObject;

public class LanguageData extends RealmObject {

    private String langKey;
    private String langName;

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }
}
