package com.choicely.csvcompanion.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SingleTranslationData extends RealmObject {

    @PrimaryKey
    private String langKey;
    private String translation;

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
