package com.choicely.csvcompanion.library_content;

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

import com.choicely.csvcompanion.LanguageData;
import com.choicely.csvcompanion.R;
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

public class LibraryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryActivity";

    private EditText libraryNameEditText;
    private EditText langCodeEditText;


    private final List<String> langList = new ArrayList<>();
    private TextView languageCountTextView;
    private RecyclerView contentRecyclerView;
    private LibraryContentAdapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final DatabaseReference librariesRef = ref.child("libraries/Library1");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_profile);

        languageCountTextView = findViewById(R.id.activity_library_profile_language_count);
        langCodeEditText = findViewById(R.id.activity_library_profile_language_field);
        libraryNameEditText = findViewById(R.id.activity_library_profile_name);

        contentRecyclerView = findViewById(R.id.activity_library_profile_recycler);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryContentAdapter(this);
        contentRecyclerView.setAdapter(adapter);

//        addLanguageToFireBase();

//        updateContent();
//        createNewLibrary();

    }

    public void createNewLibrary() {

    }

    private void updateContent() {
        adapter.clear();

        for (int i = 0; i < langList.size(); i++) {

        }

        adapter.notifyDataSetChanged();
    }

    private void addLanguageToFireBase() {
        DatabaseReference librariesRef = ref.child("libraries/Library1/languages");

        Realm realm = RealmHelper.getInstance().getRealm();
        RealmResults<LanguageData> languages = realm.where(LanguageData.class).findAll();

        Map<String, Object> langMap = new HashMap<>();

        for (LanguageData language : languages) {
            Log.d(TAG, "addLanguageToFireBase " + language.getLang());
            langMap.put(UUID.randomUUID().toString(), language.getLang());
        }
        librariesRef.setValue(langMap);
    }

    private final LanguageData languageData = new LanguageData();

    public void onAddLanguageClicked(View view) {
        @NotNull
        String langCode = langCodeEditText.getText().toString();

        Realm realm = RealmHelper.getInstance().getRealm();

        if (!checkIfLanguageAlreadyExists(langCode) && !langCode.isEmpty()) {
            languageData.setLang(langCode);
            realm.executeTransaction(realm1 -> realm.insertOrUpdate(languageData));
        } else if (!checkIfLanguageAlreadyExists(langCode) && langCode.isEmpty()) {
            Toast.makeText(this, "Language field cannot be emtpy!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Language: " + '"' + langCode + '"' + " already exists", Toast.LENGTH_SHORT).show();
        }
        addLanguageToFireBase();
    }

    private Boolean checkIfLanguageAlreadyExists(String langCode) {
        Realm realm = RealmHelper.getInstance().getRealm();
        RealmResults<LanguageData> languages = realm.where(LanguageData.class).findAll();

        for (LanguageData language : languages) {
            if (langCode.equals(language.getLang())) {
                return true;
            }
        }
        return false;
    }

    public void onNewTranslationClicked(View view) {
        Realm realm = RealmHelper.getInstance().getRealm();
        realm.executeTransaction(realm1 -> realm.deleteAll());
    }
}