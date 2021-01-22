package com.choicely.csvcompanion;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.TextData;
import com.choicely.csvcompanion.db.RealmHelper;

import java.util.List;

import io.realm.Realm;

public class EditTranslationActivity extends AppCompatActivity {

    private static final String TRANSLATION_KEY = "translation_key";
    private static final String LIBRARY_KEYC = "library_key";
    private Button anotherTranslationButton;
    private EditText translationName, transLationDesc;
    private EditText androidKey, iosKey, webKey;
    private Spinner langSpinner;
    private EditText translationValue;
    private LibraryData currentLibrary;
    private TextData currentTranslation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.edit_translation_activity);

        anotherTranslationButton.findViewById(R.id.edit_translation_act_another_translation_btn);
        translationName.findViewById(R.id.edit_translation_act_translation_name);
        transLationDesc.findViewById(R.id.edit_translation_act_translation_desc);
        androidKey.findViewById(R.id.edit_translation_act_android_key);
        iosKey.findViewById(R.id.edit_translation_act_ios_key);
        webKey.findViewById(R.id.edit_translation_act_web_key);
        langSpinner.findViewById(R.id.edit_translation_act_language_dropdown);
        translationValue.findViewById(R.id.edit_translation_act_write_translation);

        String libraryKey = getIntent().getStringExtra(LIBRARY_KEYC);
        Realm realm = RealmHelper.getInstance().getRealm();
        currentLibrary = realm.where(LibraryData.class).equalTo("libraryID", libraryKey).findFirst();

        loadLanguages();

        if (getIntent().getStringExtra(TRANSLATION_KEY).isEmpty()) {
//            if (currentLibrary.)
            createNewTranslation();
        } else {
            loadTranslation();
        }

        langSpinner.setOnItemSelectedListener(langSelectedListener);
    }

    private void loadLanguages() {
        List<String> langList = currentLibrary.getLangValues();
        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this, R.layout.language_text_layout, R.id.language_text_view, langList);
        langSpinner.setAdapter(langAdapter);
    }

    private AdapterView.OnItemSelectedListener langSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void createNewTranslation() {

    }

    private void loadTranslation() {
        currentTranslation = currentLibrary.findTranslationByID(getIntent().getStringExtra(TRANSLATION_KEY));
        translationName.setText(currentTranslation.getTranslationName());
        transLationDesc.setText(currentTranslation.getTranslationDesc());
        androidKey.setText(currentTranslation.getAndroidKey());
        iosKey.setText(currentTranslation.getIosKey());
        webKey.setText(currentTranslation.getWebKey());
    }
}
