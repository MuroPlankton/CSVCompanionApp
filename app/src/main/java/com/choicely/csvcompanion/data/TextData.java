package com.choicely.csvcompanion.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TextData extends RealmObject {

    @PrimaryKey
    private String translationKey;
    private String translationName, translationDesc;
    private String androidKey, iosKey, webKey;
    private RealmList<SingleTranslationData> translations = new RealmList<>();

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationName() {
        return translationName;
    }

    public void setTranslationName(String translationName) {
        this.translationName = translationName;
    }

    public String getTranslationDesc() {
        return translationDesc;
    }

    public void setTranslationDesc(String translationDesc) {
        this.translationDesc = translationDesc;
    }

    public String getAndroidKey() {
        return androidKey;
    }

    public void setAndroidKey(String androidKey) {
        this.androidKey = androidKey;
    }

    public String getIosKey() {
        return iosKey;
    }

    public void setIosKey(String iosKey) {
        this.iosKey = iosKey;
    }

    public String getWebKey() {
        return webKey;
    }

    public void setWebKey(String webKey) {
        this.webKey = webKey;
    }

    public RealmList<SingleTranslationData> getTranslations() {
        return translations;
    }

    public void setTranslations(RealmList<SingleTranslationData> translations) {
        this.translations = translations;
    }
}
