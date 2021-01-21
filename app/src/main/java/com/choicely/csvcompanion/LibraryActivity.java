package com.choicely.csvcompanion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class LibraryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryProfileAct";

    private EditText libraryName;
    private EditText langCodeEditText;
    private List<String> langList = new ArrayList<>();
    private LibraryContentAdapter adapter;
    private RecyclerView contentRecyclerView;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private DatabaseReference librariesRef = ref.child("libraries/Library1");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_profile);

        langCodeEditText = findViewById(R.id.activity_library_profile_language_field);
        contentRecyclerView = findViewById(R.id.activity_library_profile_recycler);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryContentAdapter(this);
        contentRecyclerView.setAdapter(adapter);

        libraryName = findViewById(R.id.activity_library_profile_name);

        addLanguageToFireBase();

//        updateContent();
//        createNewLibrary();
    }

    public void createNewLibrary() {

    }

    private void updateContent() {
        adapter.clear();

        for (int i = 0; i < langList.size(); i++) {
            adapter.add(langList.get(i));
        }

        adapter.notifyDataSetChanged();
    }

    private void addLanguageToFireBase() {
        DatabaseReference librariesRef = ref.child("libraries/Library1/languages");

        Realm realm = RealmHelper.getInstance().getRealm();
        RealmResults<LanguageData> langs = realm.where(LanguageData.class).findAll();

        Map<String, Object> langMap = new HashMap<>();

        for (LanguageData language : langs) {
            Log.d(TAG, "onCreate: " + language.getLang());
            langMap.put(UUID.randomUUID().toString(), language.getLang());
        }
        librariesRef.setValue(langMap);
    }

    public void onAddLanguageClicked(View view) {
        String langCode = langCodeEditText.getText().toString();
        langList.add(langCode);

        LanguageData languageData = new LanguageData();
        languageData.setLang(langCode);

        Realm realm = RealmHelper.getInstance().getRealm();
        realm.executeTransaction(realm1 -> {
            realm.insertOrUpdate(languageData);
        });
        Toast.makeText(this, "Language: " + langCode + " added", Toast.LENGTH_SHORT).show();
    }
}

