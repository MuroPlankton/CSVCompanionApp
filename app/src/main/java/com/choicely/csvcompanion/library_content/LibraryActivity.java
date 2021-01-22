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
import io.realm.RealmResults;
import io.realm.Sort;

public class LibraryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryActivity";

    private EditText libraryNameEditText;
    private EditText langCodeEditText;
    private EditText langEditText;

    private final List<String> translationList = new ArrayList<>();
    private TextView languageCountTextView;
    private RecyclerView contentRecyclerView;
    private LibraryContentAdapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final DatabaseReference librariesRef = ref.child("libraries/Library1");

    private final LanguageData languageData = new LanguageData();

    private String libraryID;

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
            newLibrary();
        } else {
            loadLibrary();
        }
        startFireBaseListening();
    }

    private void newLibrary() {
//        Realm realm = RealmHelper.getInstance().getRealm();
//        LibraryData library = realm.where(LibraryData.class).sort("id", Sort.DESCENDING).findFirst();
//        if(library == null){
        libraryID = String.valueOf(UUID.randomUUID());
        Log.d(TAG, "new Library created with the ID:" + libraryID);
//        }


    }

    private void loadLibrary() {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData library = realm.where(LibraryData.class).equalTo("id", libraryID).findFirst();
        Log.d(TAG, "loadPicture: library loaded with id:" + libraryID);

        libraryNameEditText.setText(library.getLibraryName());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveLibrary();
    }

    private void saveLibrary() {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData libraryData = new LibraryData();

        Log.d(TAG, "saveLibrary: library saved with the ID:" + libraryID);

        libraryData.setLibraryID(libraryID);
        libraryData.setLibraryName(libraryNameEditText.getText().toString());

        realm.executeTransaction(realm1 -> {
            realm.insertOrUpdate(libraryData);
        });
        addLibraryToFireBase();

    }

    private void addLibraryToFireBase(){
        DatabaseReference librariesRef = ref.child("libraries");
        String name = libraryNameEditText.getText().toString();
        librariesRef.push().setValue(name);
    }

    private void startFireBaseListening() {
        FirebaseDBHelper helper = FirebaseDBHelper.getInstance();
        helper.setListener(this::updateContent);
        helper.listenForLibraryDataChange();
    }

    private void updateContent() {
        adapter.clear();

        Realm realm = RealmHelper.getInstance().getRealm();
        RealmResults<SingleTranslationData> translations = realm.where(SingleTranslationData.class).findAll();

        for (SingleTranslationData singleTranslation : translations) {
            Log.d(TAG, "updateContent: " + singleTranslation.getTranslation());
            adapter.add(singleTranslation.getTranslation());
        }

        adapter.notifyDataSetChanged();
    }

    public void onAddLanguageClicked(View view) {
        @NotNull
        String langCode = langCodeEditText.getText().toString();
        String language = langEditText.getText().toString();

        Realm realm = RealmHelper.getInstance().getRealm();

        if (!checkIfLanguageAlreadyExists(langCode) && !langCode.isEmpty()) {
            languageData.setLangKey(langCode);
            languageData.setLangName(language);
            realm.executeTransaction(realm1 -> realm.insertOrUpdate(languageData));
            Toast.makeText(this, "Language: " + '"' + langCode + '"' + " added", Toast.LENGTH_SHORT).show();
            addLanguageToFireBase();

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

    private void addLanguageToFireBase() {
        DatabaseReference librariesRef = ref.child("libraries/Library1/languages");

        Realm realm = RealmHelper.getInstance().getRealm();
        RealmResults<LanguageData> languages = realm.where(LanguageData.class).findAll();

        Map<String, String> langMap = new HashMap<>();

        for (LanguageData language : languages) {
            Log.d(TAG, "Amount of languages: " + languages.size());
            langMap.put(language.getLangKey(), language.getLangName());
        }
        Log.d(TAG, "addLanguageToFireBase: " + langMap);
        librariesRef.setValue(langMap);
    }


    public void onNewTranslationClicked(View view) {
        Intent intent = new Intent(LibraryActivity.this, EditTranslationActivity.class);
        startActivity(intent);
    }
}