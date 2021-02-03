package com.choicely.csvcompanion.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

@SuppressWarnings("unchecked")
public class FirebaseDBHelper {

    private static final String TAG = "FirebaseDBHelper";
    private static FirebaseDBHelper instance;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private onDatabaseUpdateListener listener;


    public FirebaseDBHelper() {
    }

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException(TAG + " is already initialized!");
        }

        instance = new FirebaseDBHelper();
    }

    public static FirebaseDBHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException(TAG + " is not initialized!");
        }

        return instance;
    }

    public void listenForUserLibraryDataChange() {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String currentUserString = currentUser.getUid();
                DatabaseReference myRef = database.getReference("user_libraries").child(currentUserString);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final Object changedData = snapshot.getValue();
                        Log.d(TAG, "onDataChange: " + changedData);
//                        readUserLibraries(changedData);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read users library values", error.toException());
                    }
                });
            }
    }

    public void updateLibrary(String libraryID) {
        DatabaseReference myRef = database.getReference("libraries").child(libraryID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Object changedData = snapshot.getValue();
                loadSingleLibraryContent(changedData, libraryID);
                Log.w(TAG, "onDataChange: " + changedData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read libraries value", error.toException());
            }
        });
    }

    public void loadSingleLibraryContent(Object library, String libraryID) {
        final Map<String, Object> libraryMap = (Map<String, Object>) library;
        Object languagesObject = libraryMap.get("languages");
        Map<String, Object> languagesMap = (Map<String, Object>) languagesObject;
        RealmList<LanguageData> languageDataRealmList = new RealmList<>();
        
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        realm.executeTransaction(realm1 -> {
            if (languagesMap != null) {
                for (String langKey : languagesMap.keySet()) {
                    Object languageValue = languagesMap.get(langKey);

                    LanguageData language = new LanguageData();
                    language.setLangKey(langKey);
                    language.setLangName((String) languageValue);
                    languageDataRealmList.add(language);

                    libraryData.setLanguages(languageDataRealmList);
                }
            }

            Object textsObject = libraryMap.get("texts");
            Map<String, Object> textsMap = (Map<String, Object>) textsObject;
            RealmList<TextData> textDataRealmList = new RealmList<>();

            if (textsMap != null) {
                for (String key2 : textsMap.keySet()) {
                    Object textObject = textsMap.get(key2);
                    Map<String, Object> textMap = (Map<String, Object>) textObject;

                    if (textMap != null) {
                        TextData text = new TextData();

                        text.setTextKey(key2);
                        text.setTranslationName((String) textMap.get("name"));
                        text.setTranslationDesc((String) textMap.get("description"));
                        textDataRealmList.add(text);
                        libraryData.setTexts(textDataRealmList);
                    }
                }
            }
            realm.insertOrUpdate(libraryData);
        });

        if (listener != null) {
            listener.onDatabaseUpdate();
        }
    }

    public void readAndUpdateSingleText(String libraryKey, String TextKey) {
        DatabaseReference textReference = FirebaseDatabase.getInstance().getReference().child("libraries").child(libraryKey).child("texts").child(TextKey);

        textReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Map<String, Object> textMap = (Map<String, Object>) snapshot.getValue();
                addTextToRealm(textMap, libraryKey, TextKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addTextToRealm(Map textSnapshot, String libraryKey, String textKey) {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData library = realm.where(LibraryData.class).equalTo("libraryID", libraryKey).findFirst();
        RealmList<TextData> texts = library.getTexts();
        TextData text = texts.where().equalTo("textKey", textKey).findFirst();
        int textIndex = texts.indexOf(text);

        realm.beginTransaction();
        text.setAndroidKey(textSnapshot.get("android_key").toString());
        text.setIosKey(textSnapshot.get("ios_key").toString());
        text.setWebKey(textSnapshot.get("web_key").toString());

        Map<String, Object> translationsMap = (Map<String, Object>) textSnapshot.get("translations");
        RealmList<SingleTranslationData> translations = new RealmList<>();

        for (Object translationLangKey : textSnapshot.keySet()) {
            SingleTranslationData data = new SingleTranslationData();
            data.setLangKey(translationLangKey.toString());
            data.setTranslation(translationsMap.get(translationLangKey).toString());
            translations.add(data);
        }

        text.setTranslations(translations);
        texts.set(textIndex, text);
        library.setTexts(texts);
        realm.insertOrUpdate(library);
        realm.commitTransaction();
    }

    public void setListener(onDatabaseUpdateListener listener) {
        this.listener = listener;
    }

    public interface onDatabaseUpdateListener {
        void onDatabaseUpdate();
    }
}