package com.choicely.csvcompanion.library_content;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.EditTranslationActivity;
import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class LibraryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryActivity";

    private EditText libraryNameEditText;
    private EditText langCodeEditText;
    private EditText langEditText;

    private final List<String> translationList = new ArrayList<>();
//    private List<LanguageData> languageList = new ArrayList<>();

    private TextView languageCountTextView;
    private RecyclerView contentRecyclerView;
    private LibraryContentAdapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();

    private final LanguageData languageData = new LanguageData();

    private String libraryID;
    private String libraryName;
    private List<LanguageData> languageList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_profile);

        languageCountTextView = findViewById(R.id.activity_library_profile_language_count);
        langCodeEditText = findViewById(R.id.activity_library_profile_language_code_field);
        langEditText = findViewById(R.id.activity_library_profile_language_field);
        libraryNameEditText = findViewById(R.id.activity_library_profile_name);

        contentRecyclerView = findViewById(R.id.activity_library_profile_recycler);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryContentAdapter(this);
        contentRecyclerView.setAdapter(adapter);

        libraryID = getIntent().getStringExtra(IntentKeys.LIBRARY_ID);

        if (libraryID == null) {
            Log.d(TAG, "onCreate: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   uusi kirjasto");
            newLibrary();
        } else {
            Log.d(TAG, "onCreate: ############################################################################### ladattu kirjasto");
            loadLibrary();
        }
        startFireBaseListening();
    }

    private void newLibrary() {
        DatabaseReference createdLibrary = ref.child("libraries/" + UUID.randomUUID() + "/languages");
        libraryID = String.valueOf(UUID.randomUUID());
        Log.d(TAG, "new Library created with the ID:" + libraryID);
    }

    private void loadLibrary() {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData library = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();
        Log.d(TAG, "loadPicture: library loaded with id:" + libraryID);
        Log.d(TAG, "loadLibrary: library name: " + library.getLibraryName());
        libraryNameEditText.setText(library.getLibraryName());
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        saveLibrary();
//    }

    private void saveLibrary() {
        DatabaseReference libRef = ref.child("libraries/" + libraryID);
        Map<String, Object> library = new HashMap<>();
        library.put("library_name", libraryNameEditText.getText().toString());
        libRef.updateChildren(library);

        Log.d(TAG, "saveLibrary: library saved with the ID:" + libraryID);
    }

    private void startFireBaseListening() {
        FirebaseDBHelper helper = FirebaseDBHelper.getInstance();
        helper.setListener(this::updateContent);
        helper.listenForLibraryDataChange();
    }

    private void updateContent() {
        adapter.clear();

        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData library = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        try {
            List<TextData> textList = library.getTexts();
            for (TextData text : textList) {
                adapter.addValues(UUID.randomUUID().toString(), text.getTranslationName(), text.getTranslationDesc());
            }
            adapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public void onAddLanguageClicked(View view) {

        @NotNull
        String langCode = langCodeEditText.getText().toString();
        String language = langEditText.getText().toString();
        libraryName = libraryNameEditText.getText().toString();

        if (!checkIfLanguageAlreadyExists(langCode) && !langCode.isEmpty()) {

            addLanguageToFireBase(langCode, language);
            Toast.makeText(this, "Language: " + '"' + langCode + '"' + " added", Toast.LENGTH_SHORT).show();

        } else if (!checkIfLanguageAlreadyExists(langCode) && langCode.isEmpty()) {
            Toast.makeText(this, "Language code field cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Language: " + '"' + langCode + '"' + " already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean checkIfLanguageAlreadyExists(String langCode) {
        Realm realm = RealmHelper.getInstance().getRealm();
        RealmResults<LanguageData> languages = realm.where(LanguageData.class).findAll();

        for (LanguageData language : languages) {
            if (langCode.equals(language.getLangKey())) {
                return true;
            }
        }
        return false;
    }

    private void addLanguageToFireBase(String langCode, String langName) {
        DatabaseReference librariesRef = ref.child("libraries/" + libraryID + "/languages");
        Map<String, Object> langMap = new HashMap<>();
        langMap.put(langCode, langName);
        librariesRef.updateChildren(langMap);
    }


    public void onNewTranslationClicked(View view) {
        Intent intent = new Intent(LibraryActivity.this, EditTranslationActivity.class);
        intent.putExtra(IntentKeys.LIBRARY_ID, libraryID);
        startActivity(intent);
        saveLibrary();
//        Realm realm = RealmHelper.getInstance().getRealm();
//        realm.executeTransaction(realm1 -> realm.deleteAll());
    }
}