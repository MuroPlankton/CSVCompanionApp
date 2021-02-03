package com.choicely.csvcompanion.db;

import android.util.Log;

import androidx.annotation.NonNull;

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

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

@SuppressWarnings("unchecked")
public class FirebaseDBHelper {

    private static final String TAG = "FirebaseDBHelper";
    private static FirebaseDBHelper instance;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
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

    public void listenForUserLibraryDataChange() {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String currentUserString = currentUser.getUid();
                DatabaseReference myRef = database.getReference("user_libraries").child(currentUserString);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final Object changedData = snapshot.getValue();
                        updateUserLibraryData(changedData);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read users library values", error.toException());
                    }
                });
            }
    }

    private void updateUserLibraryData(Object userLibrary) {
        if (userLibrary instanceof Map) {
            final Map<String, Object> userLibraryMap = (Map<String, Object>) userLibrary;

            realm.executeTransaction(realm1 -> {
                if (userLibraryMap != null) {
                    for (String key : userLibraryMap.keySet()) {
                        Object libraryName = userLibraryMap.get(key);

                        LibraryData libraryData = new LibraryData();
                        libraryData.setLibraryID(key);
                        libraryData.setLibraryName((String) libraryName);
                        realm.copyToRealmOrUpdate(libraryData);
                    }
                }
            });
        }

        if (listener != null) {
            listener.onDatabaseUpdate();
        }
    }

    public void updateLibrary(String libraryID) {
        DatabaseReference myRef = database.getReference("libraries").child(libraryID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Object changedData = snapshot.getValue();
                loadSingleLibraryContent(changedData);
                Log.w(TAG, "onDataChange: " + changedData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read libraries value", error.toException());
            }
        });
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
            realm.copyToRealmOrUpdate(libraryData);
        });

        if (listener != null) {
            listener.onDatabaseUpdate();
        }
    }

    public void setListener(onDatabaseUpdateListener listener) {
        this.listener = listener;
    }

    public interface onDatabaseUpdateListener {
        void onDatabaseUpdate();
    }
}