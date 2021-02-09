package com.choicely.csvcompanion.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.choicely.csvcompanion.data.InboxMessageData;
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
    private final Realm realm = RealmHelper.getInstance().getRealm();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private onDatabaseUpdateListener listener;
    private onSingleTextLoadedListener textLoadedListener;

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
                for (String key : userLibraryMap.keySet()) {
                    Object libraryName = userLibraryMap.get(key);

                    LibraryData libraryData = new LibraryData();
                    libraryData.setLibraryID(key);
                    libraryData.setLibraryName((String) libraryName);
//                    Log.w(TAG, "updateUserLibraryData: " + libraryData.getLibraryName() + " " + libraryData.getLibraryID());
                    realm.copyToRealmOrUpdate(libraryData);
                }
            });
        }

        if (listener != null) {
            listener.onDatabaseUpdate();
        }
    }

    public void listenForLibraryDataChange(String libraryID) {
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
        if (library instanceof Map) {
            final Map<String, Object> libraryMap = (Map<String, Object>) library;
            Object languagesObject = libraryMap.get("languages");

            Map<String, Object> languagesMap = (Map<String, Object>) languagesObject;
            RealmList<LanguageData> languageDataRealmList = new RealmList<>();

            Object textsObject = libraryMap.get("texts");
            Map<String, Object> textsMap = (Map<String, Object>) textsObject;
            RealmList<TextData> textDataRealmList = new RealmList<>();

            LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

            realm.beginTransaction();

            //TODO: Code is repeated here. Should be changed later somehow

            if (libraryData == null) {
                libraryData = new LibraryData();
                libraryData.setLibraryID(libraryID);
                libraryData.setLibraryName((String) libraryMap.get("library_name"));

                RealmList<LanguageData> languageRealmList = new RealmList<>();

                if (languagesMap != null) {
                    for (String key : languagesMap.keySet()) {
                        Object languageValue = languagesMap.get(key);

                        LanguageData languageData = new LanguageData();
                        languageData.setLangKey(key);
                        languageData.setLangName((String) languageValue);
                        languageRealmList.add(realm.copyToRealmOrUpdate(languageData));
                        libraryData.setLanguages(languageRealmList);
                    }
                }

                RealmList<TextData> textRealmList = new RealmList<>();

                if (textsMap != null) {
                    for (String key2 : textsMap.keySet()) {
                        Object textObject = textsMap.get(key2);
                        Map<String, Object> textMap = (Map<String, Object>) textObject;

                        if (textMap != null) {
                            TextData text = new TextData();

                            text.setTextKey(key2);
                            text.setTranslationName((String) textMap.get("name"));
                            text.setTranslationDesc((String) textMap.get("description"));
                            textRealmList.add(realm.copyToRealmOrUpdate(text));
                            libraryData.setTexts(textRealmList);
                        }
                    }
                }
                realm.copyToRealmOrUpdate(libraryData);
            }

            if (languagesMap != null) {
                for (String langKey : languagesMap.keySet()) {
                    Object languageValue = languagesMap.get(langKey);

                    LanguageData language = new LanguageData();
                    language.setLangKey(langKey);
                    language.setLangName((String) languageValue);
                    languageDataRealmList.add(realm.copyToRealmOrUpdate(language));
                    libraryData.setLanguages(languageDataRealmList);
                }
            }

            if (textsMap != null) {
                for (String key2 : textsMap.keySet()) {
                    Object textObject = textsMap.get(key2);
                    Map<String, Object> textMap = (Map<String, Object>) textObject;

                    if (textMap != null) {
                        TextData text = new TextData();

                        text.setTextKey(key2);
                        text.setTranslationName((String) textMap.get("name"));
                        text.setTranslationDesc((String) textMap.get("description"));
                        textDataRealmList.add(realm.copyToRealmOrUpdate(text));
                        libraryData.setTexts(textDataRealmList);
                    }
                }
            }
            realm.copyToRealmOrUpdate(libraryData);
            realm.commitTransaction();

            if (listener != null) {
                listener.onDatabaseUpdate();
            }
        }
    }

    public void readAndUpdateSingleText(String libraryKey, String TextKey) {
        DatabaseReference textReference = FirebaseDatabase.getInstance().getReference().child("libraries").child(libraryKey).child("texts").child(TextKey);

        textReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Map<String, Object> textMap = (Map<String, Object>) snapshot.getValue();
                if (textMap != null) {
                    addTextToRealm(textMap, libraryKey, TextKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read text value", error.toException());
            }
        });
    }

    public void addTextToRealm(Map<String, Object> textMap, String libraryKey, String textKey) {
        LibraryData library = realm.where(LibraryData.class).equalTo("libraryID", libraryKey).findFirst();

        if (library != null) {
            RealmList<TextData> texts = library.getTexts();
            TextData text = texts.where().equalTo("textKey", textKey).findFirst();
            int textIndex = texts.indexOf(text);

            if (text != null) {
                realm.beginTransaction();
                text.setAndroidKey(textMap.get("android_key").toString());
                text.setIosKey(textMap.get("ios_key").toString());
                text.setWebKey(textMap.get("web_key").toString());

                if (textMap.get("translations") != null) {
                    Map<String, Object> translationsMap = (Map<String, Object>) textMap.get("translations");
                    RealmList<SingleTranslationData> translations = new RealmList<>();

                    for (String translationLangKey : translationsMap.keySet()) {
                        SingleTranslationData data = new SingleTranslationData();
                        data.setLangKey(translationLangKey);
                        data.setTranslation(translationsMap.get(translationLangKey).toString());
                        translations.add(realm.copyToRealmOrUpdate(data));
                    }
                    text.setTranslations(translations);
                }

                texts.set(textIndex, text);
                library.setTexts(texts);
                realm.insertOrUpdate(library);
                realm.commitTransaction();
                textLoadedListener.onSingleTextLoaded();
            }
        }
    }

    public void listenForUserInboxDataChange() {
        if (currentUser != null) {
            String currentUserString = currentUser.getUid();
            DatabaseReference ref = database.getReference("user_inbox").child(currentUserString);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final Object changedData = snapshot.getValue();
                    updateUserInboxData(changedData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read users inbox values", error.toException());
                }
            });
        }
    }

    private void updateUserInboxData(Object userInbox) {
        if (userInbox instanceof Map) {
            final Map<String, Object> userInboxMap = (Map<String, Object>) userInbox;

            realm.executeTransaction(realm1 -> {
                for (String key : userInboxMap.keySet()) {
                    InboxMessageData message = new InboxMessageData();
                    message.setLibraryID(key);

                    Object messageContent = userInboxMap.get(key);
                    Map<String, Object> messageMap = (Map<String, Object>) messageContent;

                    if (messageMap != null) {
                        message.setLibraryName((String) messageMap.get("library_name"));
                        message.setCustomMessage((String) messageMap.get("custom_message"));
                        message.setSenderName((String) messageMap.get("sender_name"));
                    }

                    realm.copyToRealmOrUpdate(message);
                }
            });
        }

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

    public void setTextLoadListener(onSingleTextLoadedListener listener) {
        this.textLoadedListener = listener;
    }

    public interface onSingleTextLoadedListener {
        void onSingleTextLoaded();
    }
}