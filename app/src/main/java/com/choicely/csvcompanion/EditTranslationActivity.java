package com.choicely.csvcompanion;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;
import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.realm.Realm;

import static com.choicely.csvcompanion.IntentKeys.LIBRARY_ID;
import static com.choicely.csvcompanion.IntentKeys.TRANSLATION_ID;

public class EditTranslationActivity extends AppCompatActivity {

    private String CurrentLibraryKey;
    private String currentTextKey;
    private Button anotherTranslationButton;
    private EditText translationName, transLationDesc;
    private EditText androidKey, iosKey, webKey;
    private Spinner langSpinner;
    private EditText translationValue;
    private Button submitTranslationButton;
    private LibraryData currentLibrary;
    private TextData currentText;
    private List<String> langNames = new ArrayList<>();
    private List<String> langKeys = new ArrayList<>();
    private boolean isTranslationSaveScheduled = false;
    private Timer translationTextSaveTimer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_translation_activity);

        CurrentLibraryKey = getIntent().getStringExtra(LIBRARY_ID);
        currentTextKey = getIntent().getStringExtra(TRANSLATION_ID);

        anotherTranslationButton = findViewById(R.id.edit_translation_act_another_translation_btn);
        translationName = findViewById(R.id.edit_translation_act_translation_name);
        transLationDesc = findViewById(R.id.edit_translation_act_translation_desc);
        androidKey = findViewById(R.id.edit_translation_act_android_key);
        iosKey = findViewById(R.id.edit_translation_act_ios_key);
        webKey = findViewById(R.id.edit_translation_act_web_key);
        langSpinner = findViewById(R.id.edit_translation_act_language_dropdown);
        translationValue = findViewById(R.id.edit_translation_act_write_translation);
        submitTranslationButton = findViewById(R.id.edit_translation_act_submit_translation);

        String libraryKey = getIntent().getStringExtra(LIBRARY_ID);
        Realm realm = RealmHelper.getInstance().getRealm();
        currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", libraryKey).findFirst();

        if (currentTextKey != null) {
            findCurrentText();
            loadText();
        } else {
            currentTextKey = UUID.randomUUID().toString();
        }
        loadLanguages();

        langSpinner.setOnItemSelectedListener(langSelectedListener);

        translationValue.addTextChangedListener(translationTextChangedListener);

        anotherTranslationButton.setOnClickListener(buttonListener);
        submitTranslationButton.setOnClickListener(buttonListener);
    }

    private void loadLanguages() {
        List<LanguageData> languages = currentLibrary.getLanguages();
        for (LanguageData language : languages) {
            langNames.add(language.getLangName());
            langKeys.add(language.getLangKey());
        }
        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this, R.layout.language_text_layout, R.id.language_text_view, langNames);
        langSpinner.setAdapter(langAdapter);
    }

    private AdapterView.OnItemSelectedListener langSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Realm realm = RealmHelper.getInstance().getRealm();

            currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", getIntent().getStringExtra(LIBRARY_ID)).findFirst();
            List<TextData> updatedTexts = currentLibrary.getTexts();
            if (updatedTexts.size() > 0) {
                List<SingleTranslationData> updatedTranslations = null;

                for (TextData text : updatedTexts) {
                    if (text.getTextKey().equals(currentTextKey)) {
                        currentText = text;
                        updatedTranslations = text.getTranslations();
                        break;
                    }
                }

                if (updatedTranslations != null) {
                    for (SingleTranslationData translationData : updatedTranslations) {
                        if (translationData.getLangKey().equals(langKeys.indexOf(position))) {
                            translationValue.setText(translationData.getTranslation());
                        }
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void findCurrentText() {
        List<TextData> texts = currentLibrary.getTexts();
        for (TextData text : texts) {
            if (text.getTextKey().equals(currentTextKey)) {
                currentText = text;
                break;
            }
        }
    }

    private void loadText() {
        translationName.setText(currentText.getTranslationName());
        transLationDesc.setText(currentText.getTranslationDesc());
        androidKey.setText(currentText.getAndroidKey());
        iosKey.setText(currentText.getIosKey());
        webKey.setText(currentText.getWebKey());
    }

    private TextWatcher translationTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                translationTextSaveDelayer();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void translationTextSaveDelayer() {
        TimerTask translationSaveTask = new TimerTask() {
            @Override
            public void run() {
                saveCurrentTranslationTextToFirebase();
            }
        };

        if (!isTranslationSaveScheduled) {
            isTranslationSaveScheduled = true;
            translationTextSaveTimer = new Timer();
            translationTextSaveTimer.schedule(translationSaveTask, 2000);
        } else {
            isTranslationSaveScheduled = false;
            translationTextSaveTimer.cancel();
            translationTextSaveTimer.purge();
            translationTextSaveDelayer();
        }
    }

    private void saveCurrentTranslationTextToFirebase() {
        FirebaseDatabase.getInstance().getReference()
                .child("libraries").child(CurrentLibraryKey)
                .child("texts").child(currentTextKey)
                .child(langKeys.get(langSpinner.getSelectedItemPosition()))
                .setValue(translationValue.getText().toString());
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.edit_translation_act_another_translation_btn) {
                clearAndCreateNew();
            } else {
                onBackPressed();
            }
        }
    };

    @Override
    public void onBackPressed() {
        saveCurrentText();
        super.onBackPressed();
    }

    private void saveCurrentText() {
        Map<String, String> textToSave = new HashMap<>();
        textToSave.put("name", translationName.getText().toString());
        textToSave.put("description", transLationDesc.getText().toString());
        textToSave.put("android_key", androidKey.getText().toString());
        textToSave.put("ios_key", iosKey.getText().toString());
        textToSave.put("web_key", webKey.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("libraries").child(CurrentLibraryKey).child("texts").child(currentTextKey).setValue(textToSave);
    }

    private void clearAndCreateNew() {
        translationName.setText("");
        transLationDesc.setText("");
        androidKey.setText("");
        iosKey.setText("");
        webKey.setText("");
        translationValue.setText("");

        currentTextKey = UUID.randomUUID().toString();
    }
}
