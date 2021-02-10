package com.choicely.csvcompanion.libraryContent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.CSVWriter;
import com.choicely.csvcompanion.EditTranslationActivity;
import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.TextData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.choicely.csvcompanion.popups.PopUpAlert;
import com.choicely.csvcompanion.popups.SharePopup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;

public class LibraryActivity extends AppCompatActivity {

    private static final String TAG = "LibraryActivity";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseDBHelper firebaseDBHelper = FirebaseDBHelper.getInstance();
    private final Realm realm = RealmHelper.getInstance().getRealm();

    private EditText libraryNameEditText;
    private EditText langCodeEditText;
    private EditText langEditText;
    private ImageView addLanguageIcon;
    private Button newTranslationButton;
    private TextView languageCountTextView;

    private ListPopupWindow languagePopupWindow;
    private RecyclerView langContentRecycler;
    private final List<Pair<String, String>> sampleLanguageList = new ArrayList<>();

    private LibraryContentAdapter adapter;

    private LibraryData currentLibrary;
    private String libraryID;
    private int languageCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);

        langCodeEditText = findViewById(R.id.library_activity_language_code_field);
        langEditText = findViewById(R.id.library_activity_language_field);
        languagePopupWindow = new ListPopupWindow(this);
        languagePopupWindow.setAdapter(new ArrayAdapter<>(this,
                R.layout.language_text_layout, R.id.language_text_view,
                new String[] {"en | English", "fi | Suomi", "sv | Svenska", "ee | Eestlane", "it | Italiano"}));
        languagePopupWindow.setAnchorView(langCodeEditText);
        languagePopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        languagePopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        libraryNameEditText = findViewById(R.id.library_activity_library_name);
        languageCountTextView = findViewById(R.id.library_activity_language_count);
        addLanguageIcon = findViewById(R.id.library_activity_add_lang_icon);
        newTranslationButton = findViewById(R.id.library_activity_new_translation_button);
        langCodeEditText = findViewById(R.id.library_activity_language_code_field);
        langEditText = findViewById(R.id.library_activity_language_field);

        langContentRecycler = findViewById(R.id.library_activity_recycler);
        langContentRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryContentAdapter(this);
        langContentRecycler.setAdapter(adapter);

        libraryID = getIntent().getStringExtra(IntentKeys.LIBRARY_ID);

        if (libraryID == null) {
            newLibrary();
        } else {
            loadLibrary();
        }

        sampleLanguageList.add(new Pair<>("en", "English"));
        sampleLanguageList.add(new Pair<>("fi", "Suomi"));
        sampleLanguageList.add(new Pair<>("sv", "Svenska"));
        sampleLanguageList.add(new Pair<>("ee", "Eestlane"));
        sampleLanguageList.add(new Pair<>("it", "Italiano"));

        langEditText.setOnFocusChangeListener(onFocusChangeListener);
        langCodeEditText.setOnFocusChangeListener(onFocusChangeListener);
        languagePopupWindow.setOnItemClickListener(langPopupItemClickListener);

        setOnLanguageAddedListener(() -> {
            languageCount += 1;
            languageCountTextView.setText(String.format(Locale.getDefault(), "Amount of languages: %d", languageCount));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library_activity_actions, menu);
        return true;
    }

    private AdapterView.OnItemClickListener langPopupItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            langCodeEditText.setText(sampleLanguageList.get(position).first);
            langEditText.setText((sampleLanguageList.get(position).second));
            languagePopupWindow.dismiss();
        }
    };

    @Override
    protected void onResume() {
        try {
            super.onResume();
            updateContent();
            startFireBaseListening();
        } catch (NullPointerException e) {
            Log.e(TAG, "onResume: ", e);
        }
    }

    private void startFireBaseListening() {
        firebaseDBHelper.setListener(this::updateContent);
        firebaseDBHelper.listenForLibraryDataChange(libraryID);
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !languagePopupWindow.isShowing()) {
                languagePopupWindow.show();
            }
        }
    };

    public void onClick(View v) {
        if (v == addLanguageIcon) {
            addLanguage();
        } else if (v == newTranslationButton && !checkIfNameIsEmpty()) {
            newTranslation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            new SharePopup(this, libraryID, this);
            return true;
        } else if (item.getItemId() == R.id.action_export) {
            saveLibrary();
            CSVWriter writer = new CSVWriter(libraryID, this);
            updateCurrentLibrary();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void newLibrary() {
        libraryID = String.valueOf(UUID.randomUUID());
        Log.w(TAG, "newLibrary: " + libraryID);
        languageCountTextView.setText(String.format(Locale.getDefault(), "Amount of languages: %d", languageCount));

        addUser();
        saveLibrary();
    }

    private void loadLibrary() {
        updateCurrentLibrary();
        if (currentLibrary != null) {
            startFireBaseListening();
            libraryNameEditText.setText(currentLibrary.getLibraryName());
        }
    }

    @Override
    public void onBackPressed() {
        if (!checkIfNameIsEmpty()) {
            super.onBackPressed();
            saveLibrary();
        }
    }

    private void saveLibrary() {
        addUser();
        DatabaseReference libRef = ref.child("libraries/" + libraryID);

        String libraryName = libraryNameEditText.getText().toString();

        Map<String, Object> library = new HashMap<>();
        library.put("library_name", libraryName);
        libRef.updateChildren(library);
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

        updateLanguageTextCount();
        try {
            List<TextData> textList = currentLibrary.getTexts();
            adapter.setLibrary(currentLibrary);
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
            languageAddedListener.onLanguageAdded();

            firebaseDBHelper.listenForLibraryDataChange(libraryID);
        } else if (!checkIfLanguageAlreadyExists(langCode) && langCode.isEmpty()) {
            Toast.makeText(this, "Language code field cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Language: " + '"' + langCode + '"' + " already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void addLanguageToFireBase(String langCode, String langName) {
        DatabaseReference librariesRef = ref.child("libraries/" + libraryID + "/languages");
        Map<String, Object> langMap = new HashMap<>();
        langMap.put(langCode, langName);
        librariesRef.updateChildren(langMap);

        firebaseDBHelper.listenForLibraryDataChange(libraryID);
        clearLanguageEditTexts();
    }

    private boolean checkIfLanguageAlreadyExists(String langCode) {
        if (currentLibrary != null) {
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

    public void newTranslation() {
        saveLibrary();
        firebaseDBHelper.listenForLibraryDataChange(libraryID);
        updateCurrentLibrary();

        if (currentLibrary != null && currentLibrary.getLanguages().size() == 0) {
            Toast.makeText(this, "No languages", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(LibraryActivity.this, EditTranslationActivity.class);
            intent.putExtra(IntentKeys.LIBRARY_ID, libraryID);
            startActivity(intent);
        }
    }

    private void updateCurrentLibrary() {
        currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();
    }

    private void updateLanguageTextCount() {
        if (currentLibrary != null) {
            languageCount = currentLibrary.getLanguages().size();
            languageCountTextView.setText(String.format(Locale.getDefault(), "Amount of languages: %d", languageCount));
        }
    }

    private void clearLanguageEditTexts() {
        langCodeEditText.getText().clear();
        langEditText.getText().clear();
    }

    private final PopUpAlert popUpAlert = new PopUpAlert();

    private boolean checkIfNameIsEmpty() {
        if (libraryNameEditText.getText().toString().isEmpty()) {
            popUpAlert.alertPopUp(this, R.string.pop_up_message_library_activity, "Warning");
            return true;
        }
        return false;
    }

    private LanguageAddedListener languageAddedListener;

    public void setOnLanguageAddedListener(LanguageAddedListener listener) {
        this.languageAddedListener = listener;
    }

    public interface LanguageAddedListener {
        void onLanguageAdded();
    }
}