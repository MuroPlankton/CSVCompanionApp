package com.choicely.csvcompanion.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.choicely.csvcompanion.FireBaseParameters;
import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.TextData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

@SuppressWarnings("unchecked")
public class FirebaseDBHelper {

    private static final String TAG = "FirebaseDBHelper";
    private static FirebaseDBHelper instance;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final LibraryData libraryData = new LibraryData();
    private final List<String> libIDList = new ArrayList<>();
    private final Realm realm = RealmHelper.getInstance().getRealm();

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

    public void listenForUserLibraryDataChange(int parameter) {
        new Thread(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String currentUserString = currentUser.getUid();
                DatabaseReference myRef = database.getReference("user_libraries").child(currentUserString);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final Object changedData = snapshot.getValue();
                        Log.d(TAG, "onDataChange: " + changedData);
                        readUserLibraries(changedData, parameter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read users library values", error.toException());
                    }
                });
            }
        }).start();
    }

    public void readUserLibraries(Object userLibraries, int parameter) {
        if (userLibraries instanceof Map) {
            final Map<String, Object> userLibrariesMap = (Map<String, Object>) userLibraries;
            libIDList.clear();
            libIDList.addAll(userLibrariesMap.keySet());
            listenForLibraryDataChange(parameter);
        }
    }

    public void listenForLibraryDataChange(int parameter) {
        for (String libID : libIDList) {
            DatabaseReference myRef = database.getReference("libraries").child(libID);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final Object changedData = snapshot.getValue();
                    Log.d(TAG, "onDataChange: PARAMETER: " + parameter);
                    if (parameter == FireBaseParameters.MAIN_ACTIVITY) {
                        loadLibraryNameAndID(changedData);
                    } else if (parameter == FireBaseParameters.LIBRARY_ACTIVITY) {
                        loadSingleLibraryContent(changedData);
                    }
//                    Log.d(TAG, "onDataChange: " + changedData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to read libraries value", error.toException());
                }
            });
        }
    }

    public void loadLibraryNameAndID(Object library) {
        if (library instanceof Map) {
            final Map<String, Object> libraryMap = (Map<String, Object>) library;

            RealmHelper helper = RealmHelper.getInstance();
            Realm realm = helper.getRealm();

            realm.executeTransaction(realm1 -> {
                for (String libID : libIDList) {

                    libraryData.setLibraryID(libID);
                }
                libraryData.setLibraryName((String) libraryMap.get("library_name"));

                Log.d(TAG, "loadLibraryNameAndID: " + libraryData.getLibraryName() + " " + libraryData.getLibraryID());
                realm.copyToRealmOrUpdate(libraryData);
            });

            if (listener != null) {
                listener.onDatabaseUpdate();
            }
        }
    }

    public void loadSingleLibraryContent(Object library) {
        final Map<String, Object> libraryMap = (Map<String, Object>) library;
        Object languagesObject = libraryMap.get("languages");
        Map<String, Object> languagesMap = (Map<String, Object>) languagesObject;
        RealmList<LanguageData> languageDataRealmList = new RealmList<>();

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

//    private void readSingleLibrary(Object library) {
//        if (library instanceof Map) {
//            final Map<String, Object> libraryMap = (Map<String, Object>) library;
//
//            RealmHelper helper = RealmHelper.getInstance();
//            Realm realm = helper.getRealm();
//
//            realm.executeTransaction(realm1 -> {
//                libraryData.setLibraryName((String) libraryMap.get("library_name"));
//
//                Object languagesObject = libraryMap.get("languages");
//                Map<String, Object> languagesMap = (Map<String, Object>) languagesObject;
//                RealmList<LanguageData> languageDataRealmList = new RealmList<>();
//
//                if (languagesMap != null) {
//                    for (String key1 : languagesMap.keySet()) {
//                        Object languageValue = languagesMap.get(key1);
//
//                        LanguageData language = new LanguageData();
//                        language.setLangKey(key1);
//                        language.setLangName((String) languageValue);
//                        languageDataRealmList.add(language);
//
//                        libraryData.setLanguages(languageDataRealmList);
//
//                        Log.d(TAG, "key: " + key1);
//                        Log.d(TAG, "languageValue: " + languageValue);
//                    }
//                }
//
//                Object textsObject = libraryMap.get("texts");
//                Log.d(TAG, "textsObject: " + textsObject);
//                Map<String, Object> textsMap = (Map<String, Object>) textsObject;
//                RealmList<TextData> textDataRealmList = new RealmList<>();
//
//                if (textsMap != null) {
//                    for (String key2 : textsMap.keySet()) {
//                        Object textObject = textsMap.get(key2);
//                        Map<String, Object> textMap = (Map<String, Object>) textObject;
//
//                        if (textMap != null) {
//                            TextData text = new TextData();
//
//                            text.setTextKey(key2);
//                            text.setTranslationName((String) textMap.get("name"));
//                            text.setTranslationDesc((String) textMap.get("description"));
//                            text.setAndroidKey((String) textMap.get("android_key"));
//                            text.setIosKey((String) textMap.get("ios_key"));
//                            text.setWebKey((String) textMap.get("web_key"));
//
//                            Object translationsObject = textMap.get("translations");
//                            Map<String, Object> translationsMap = (Map<String, Object>) translationsObject;
//                            RealmList<SingleTranslationData> translationDataRealmList = new RealmList<>();
//
//                            if (translationsMap != null) {
//                                for (String key3 : translationsMap.keySet()) {
//                                    Object translationValue = translationsMap.get(key3);
//
//                                    SingleTranslationData translation = new SingleTranslationData();
//                                    translation.setLangKey(key3);
//                                    translation.setTranslation((String) translationValue);
//
//                                    translationDataRealmList.add(translation);
//                                    text.setTranslations(translationDataRealmList);
//
//                                    Log.d(TAG, "key4: " + key3);
//                                    Log.d(TAG, "translationValue: " + translationValue);
//                                }
//                            }
//
//                            textDataRealmList.add(text);
//                            libraryData.setTexts(textDataRealmList);
//                        }
//                    }
//                }
//                realm.copyToRealmOrUpdate(libraryData);
//            });
//            if (listener != null) {
//                listener.onDatabaseUpdate();
//            }
//            ()
//        }
//    }
//
//}

    public void setListener(onDatabaseUpdateListener listener) {
        this.listener = listener;
    }

    public interface onDatabaseUpdateListener {
        void onDatabaseUpdate();
    }
}