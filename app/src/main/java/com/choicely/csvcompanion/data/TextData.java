package com.choicely.csvcompanion.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TextData extends RealmObject {

    @PrimaryKey
    private String textKey;
    private String translationName, translationDesc;
    private String androidKey, iosKey, webAdminKey, webMainKey, webWidgetKey;
    private RealmList<SingleTranslationData> translations = new RealmList<>();

    public String getTextKey() {
        return textKey;
    }

    public void setTextKey(String textKey) {
        this.textKey = textKey;
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

    public String getWebAdminKey() {
        return webAdminKey;
    }

    public void setWebAdminKey(String webAdminKey) {
        this.webAdminKey = webAdminKey;
    }

    public String getWebMainKey() {
        return webMainKey;
    }

    public void setWebMainKey(String webMainKey) {
        this.webMainKey = webMainKey;
    }

    public String getWebWidgetKey() {
        return webWidgetKey;
    }

    public void setWebWidgetKey(String webWidgetKey) {
        this.webWidgetKey = webWidgetKey;
    }

    public RealmList<SingleTranslationData> getTranslations() {
        return translations;
    }

    public void setTranslations(RealmList<SingleTranslationData> translations) {
        this.translations = translations;
    }
}
