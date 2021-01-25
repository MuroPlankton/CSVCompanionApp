package com.choicely.csvcompanion.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FirebaseDBHelper {

    private static final String TAG = "FirebaseDBHelper";
    private static FirebaseDBHelper instance;

    private onDatabaseUpdateListener listener;

    private FirebaseDBHelper() {
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

    public void listenForLibraryDataChange() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("libraries");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Object changedData = snapshot.getValue();
                readFirebaseLibraries(changedData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Failed to read value", error.toException());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void readFirebaseLibraries(Object libraries) {
        if (libraries instanceof Map) {
            final Map<String, Object> librariesMap = (Map<String, Object>) libraries;

            RealmHelper helper = RealmHelper.getInstance();
            Realm realm = helper.getRealm();

            realm.executeTransaction(realm1 -> {
                for (String key1 : librariesMap.keySet()) {
                    Object libraryObject = librariesMap.get(key1);
                    final Map<String, Object> libraryMap = (Map<String, Object>) libraryObject;

                    LibraryData library = new LibraryData();

                    if (libraryMap != null) {
//                        if(libraryMap.get("id") != null && libraryMap.get("libraryName") != null)
                        library.setLibraryID(key1);
                        library.setLibraryName((String) libraryMap.get("libraryName"));
                    }

                    Object languagesObject = libraryMap.get("languages");
                    Map<String, Object> languagesMap = (Map<String, Object>) languagesObject;
                    RealmList<LanguageData> languageDataRealmList = new RealmList<>();

                    if (languagesMap != null) {
                        for (String key2 : languagesMap.keySet()) {
                            Object languageValue = languagesMap.get(key2);

                            LanguageData language = new LanguageData();
                            language.setLangKey(key2);
                            language.setLangName((String) languageValue);
                            languageDataRealmList.add(language);

                            //I will set the list of these to libraryData
                            library.setLanguages(languageDataRealmList);

                            Log.d(TAG, "key2: " + key2);
                            Log.d(TAG, "languageValue: " + languageValue);
                        }
                    }

                    Object textsObject = libraryMap.get("texts");
                    Log.d(TAG, "textsObject: " + textsObject);
                    Map<String, Object> textsMap = (Map<String, Object>) textsObject;
                    RealmList<TextData> textDataRealmList = new RealmList<>();

                    //TODO: check properly if all this works
                    if(textsMap != null){
                        for (String key3 : textsMap.keySet()) {
                            Object textObject = textsMap.get(key3);
                            Log.d(TAG, "textObject: " + textObject);
                            Map<String, Object> textMap = (Map<String, Object>) textObject;

                            if(textMap != null) {
                                Object translationsObject = textMap.get("translations");
                                Map<String, Object> translationsMap = (Map<String, Object>) translationsObject;

                                for (String key4 : translationsMap.keySet()) {
                                    Object translationValue = translationsMap.get(key4);

                                    SingleTranslationData translation = new SingleTranslationData();
                                    translation.setLangKey(key4);
                                    translation.setTranslation((String) translationValue);

                                    Log.d(TAG, "key4: " + key4);
                                    Log.d(TAG, "translationValue: " + translationValue);
                                }

                                TextData text = new TextData();

                                //TODO: text key and possibly the translations list
//                                textData.setTextKey();
                                text.setTranslationName((String) textMap.get("name"));
                                text.setTranslationDesc((String) textMap.get("description"));
                                text.setAndroidKey((String) textMap.get("android_key"));
                                text.setIosKey((String) textMap.get("ios_key"));
                                text.setWebKey((String) textMap.get("web_key"));

                                textDataRealmList.add(text);
                                library.setTexts(textDataRealmList);
                            }
                        }
                    }
                    realm.copyToRealmOrUpdate(library);
                }
            });
            if (listener != null) {
                listener.onDatabaseUpdate();
            }
        }
    }

    public void setListener(onDatabaseUpdateListener listener) {
        this.listener = listener;
    }

    public interface onDatabaseUpdateListener {
        void onDatabaseUpdate();
    }
}