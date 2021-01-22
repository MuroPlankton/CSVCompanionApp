package com.choicely.csvcompanion.data;

import io.realm.RealmObject;

public class SingleTranslationData extends RealmObject {

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
