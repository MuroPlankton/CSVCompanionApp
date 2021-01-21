package com.choicely.csvcompanion.data;

import java.util.HashMap;
import java.util.Map;

import io.realm.annotations.PrimaryKey;

public class TranslationData {

    private String translationName, translationDesc;
    private String androidKey, iosKey, webKey;
    private Map<String, String> translations = new HashMap<>();

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

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }
}
