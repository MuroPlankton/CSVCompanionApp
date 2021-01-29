package com.choicely.csvcompanion.libraryContent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.CSVWriter;
import com.choicely.csvcompanion.EditTranslationActivity;
import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.TextData;
import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;

public class LibraryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryActivity";

    private EditText libraryNameEditText;
    private EditText langCodeEditText;
    private EditText langEditText;
    private TextView languageCountTextView;

    private Button addLanguageButton;
    private Button newTranslationButton;

    private RecyclerView contentRecyclerView;
    private LibraryContentAdapter adapter;

    private LibraryData currentLibrary;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();

    private String libraryID;
    private FirebaseUser user;

    private int languageCount = 0;

    private final Realm realm = RealmHelper.getInstance().getRealm();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);

        langCodeEditText = findViewById(R.id.library_activity_language_code_field);
        langEditText = findViewById(R.id.library_activity_language_field);
        libraryNameEditText = findViewById(R.id.library_activity_library_name);

        languageCountTextView = findViewById(R.id.library_activity_language_count);

        addLanguageButton = findViewById(R.id.library_activity_language_button);
        newTranslationButton = findViewById(R.id.library_activity_new_translation_button);

        contentRecyclerView = findViewById(R.id.library_activity_recycler);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryContentAdapter(this);
        contentRecyclerView.setAdapter(adapter);

        libraryID = getIntent().getStringExtra(IntentKeys.LIBRARY_ID);

        if (libraryID == null) {
            newLibrary();
        } else {
            loadLibrary();
        }
        setOnLanguageAddedListener(() -> {
            languageCount += 1;
            updateLanguageTextCount();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            return true;
        } else if (item.getItemId() == R.id.action_export) {
            CSVWriter.writeCSVFile(libraryID);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void newLibrary() {
        libraryID = String.valueOf(UUID.randomUUID());
        user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(TAG, "new Library created with the ID:" + libraryID);
        Log.d(TAG, "newLibrary: user:" + user);
        languageCountTextView.setText(String.format("Amount of languages: %d", languageCount));

        addUser();
        saveLibrary();
    }

    private void loadLibrary() {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData library = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();
        if (library != null) {
            libraryNameEditText.setText(library.getLibraryName());
        }
        updateContent();
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            updateContent();
            currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveLibrary();
    }

    public void onButtonClick(View v) {
        if (v == addLanguageButton) {
            addLanguage();
        } else if (v == newTranslationButton) {
            newTranslation();
        }
    }

    private void updateLanguageTextCount() {
        LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        if (libraryData != null) {
            for (int i = 0; i < libraryData.getLanguages().size(); i++) {
                languageCount = i;
            }
        }
        languageCountTextView.setText(String.format("Amount of languages: %d", languageCount));
    }

    private void saveLibrary() {
        DatabaseReference libRef = ref.child("libraries/" + libraryID);

        String libraryName = libraryNameEditText.getText().toString();

        Map<String, Object> library = new HashMap<>();
        library.put("library_name", libraryName);
        libRef.updateChildren(library);

        Log.d(TAG, "saveLibrary: library saved with the ID:" + libraryID);

        addLibraryToUserLibraries(libraryName);
    }

    private void addLibraryToUserLibraries(String libraryName) {
        DatabaseReference libRef = ref.child("user_libraries/" + user.getUid());

        Map<String, Object> userLibraryMap = new HashMap<>();
        userLibraryMap.put(libraryID, libraryName);
        libRef.updateChildren(userLibraryMap);
    }

    private void updateContent() {
        adapter.clear();

        LibraryData library = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        try {
            List<TextData> textList = library.getTexts();
            adapter.setLibrary(library);
            for (TextData text : textList) {
                adapter.add(text.getTextKey(), text.getTranslationName(), text.getTranslationDesc());
            }
            adapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void addLanguage() {
        @NotNull
        String langCode = langCodeEditText.getText().toString();
        String language = langEditText.getText().toString();

        if (!checkIfLanguageAlreadyExists(langCode) && !langCode.isEmpty()) {
            addLanguageToFireBase(langCode, language);
            Toast.makeText(this, "Language: " + '"' + langCode + '"' + " added", Toast.LENGTH_SHORT).show();

            clearLanguageEditTexts();
            updateContent();

            languageAddedListener.onLanguageAdded();
        } else if (!checkIfLanguageAlreadyExists(langCode) && langCode.isEmpty()) {
            Toast.makeText(this, "Language code field cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Language: " + '"' + langCode + '"' + " already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearLanguageEditTexts() {
        langCodeEditText.setText("");
        langEditText.setText("");
    }

    private boolean checkIfLanguageAlreadyExists(String langCode) {
//        currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        try {
            List<LanguageData> languages = currentLibrary.getLanguages();
            for (LanguageData language : languages) {
                if (langCode.equals(language.getLangKey())) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
        }
        return false;
    }

    public void addUser() {
        DatabaseReference libRef = ref.child("libraries/" + libraryID + "/users");
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(user.getUid(), user.getDisplayName());
        Log.d(TAG, "user_id: " + user.getUid());
        libRef.updateChildren(userMap);
    }

    private void addLanguageToFireBase(String langCode, String langName) {
        DatabaseReference librariesRef = ref.child("libraries/" + libraryID + "/languages");
        Map<String, Object> langMap = new HashMap<>();
        langMap.put(langCode, langName);
        librariesRef.updateChildren(langMap);
    }

    public void newTranslation() {
        saveLibrary();
        Intent intent = new Intent(LibraryActivity.this, EditTranslationActivity.class);
        intent.putExtra(IntentKeys.LIBRARY_ID, libraryID);
        startActivity(intent);
    }

    private LanguageAddedListener languageAddedListener;

    public void setOnLanguageAddedListener(LanguageAddedListener listener) {
        this.languageAddedListener = listener;
    }

    public interface LanguageAddedListener {
        void onLanguageAdded();
    }
}