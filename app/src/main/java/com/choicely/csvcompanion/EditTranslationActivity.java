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
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class EditTranslationActivity extends AppCompatActivity {

    private static final String TEXT_KEY = "translation_key";
    private static final String LIBRARY_KEYC = "library_key";
    private String CurrentLibraryKey = getIntent().getStringExtra(LIBRARY_KEYC);
    private String currentTextKey = getIntent().getStringExtra(TEXT_KEY);
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
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.edit_translation_activity);

        anotherTranslationButton = findViewById(R.id.edit_translation_act_another_translation_btn);
        anotherTranslationButton.setEnabled(false);
        translationName = findViewById(R.id.edit_translation_act_translation_name);
        transLationDesc = findViewById(R.id.edit_translation_act_translation_desc);
        androidKey = findViewById(R.id.edit_translation_act_android_key);
        iosKey = findViewById(R.id.edit_translation_act_ios_key);
        webKey = findViewById(R.id.edit_translation_act_web_key);
        langSpinner = findViewById(R.id.edit_translation_act_language_dropdown);
        translationValue = findViewById(R.id.edit_translation_act_write_translation);
        submitTranslationButton = findViewById(R.id.edit_translation_act_submit_translation);

        String libraryKey = getIntent().getStringExtra(LIBRARY_KEYC);
        Realm realm = RealmHelper.getInstance().getRealm();
        currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", libraryKey).findFirst();

        loadLanguages();
        findCurrentText();
        if (!currentTextKey.isEmpty()) {
            loadText();
        }

        langSpinner.setOnItemSelectedListener(langSelectedListener);

        translationValue.addTextChangedListener(translationTextChangedListener);

        submitTranslationButton.setOnClickListener(submitClickListener);
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

            currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", getIntent().getStringExtra(LIBRARY_KEYC)).findFirst();
            List<TextData> updatedTexts = currentLibrary.getTexts();
            List<SingleTranslationData> updatedTranslations = null;

            for (TextData text : updatedTexts) {
                if (text.getTextKey().equals(currentTextKey)) {
                    currentText = text;
                    updatedTranslations = text.getTranslations();
                    break;
                }
            }

            for (SingleTranslationData translationData : updatedTranslations) {
                if (translationData.getLangKey().equals(langKeys.indexOf(position))) {
                    translationValue.setText(translationData.getTranslation());
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
                .setValue(translationValue.getText());
    }

    private View.OnClickListener submitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Map<String, String> textToSave = new HashMap<>();
            textToSave.put("name", translationName.getText().toString());
            textToSave.put("description", transLationDesc.getText().toString());
            textToSave.put("android_key", androidKey.getText().toString());
            textToSave.put("ios_key", iosKey.getText().toString());
            textToSave.put("web_key", webKey.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("libraries").child("texts").child(currentTextKey).setValue(textToSave);
        }
    };
}
