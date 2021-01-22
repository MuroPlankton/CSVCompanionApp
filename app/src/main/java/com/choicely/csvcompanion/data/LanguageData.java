package com.choicely.csvcompanion.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LanguageData extends RealmObject {

    @PrimaryKey
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
