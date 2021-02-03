package com.choicely.csvcompanion;

import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

import static com.choicely.csvcompanion.IntentKeys.LIBRARY_ID;
import static com.choicely.csvcompanion.IntentKeys.TRANSLATION_ID;

public class EditTranslationActivity extends AppCompatActivity {

    private final PopUpAlert popUpAlert = new PopUpAlert();
    private static final String TAG = "EditTranslationActivity";

    private EditText translationName, transLationDesc;
    private EditText androidKey, iosKey, webKey;
    private Spinner langSpinner;
    private EditText translationValue;
    private Button submitTranslationButton;

    private String currentTextKey;
    private TextData currentText;
    private final List<String> langKeys = new ArrayList<>();
    private final List<String> langNames = new ArrayList<>();

    private Map<String, String> translations = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_translation_activity);

        String libraryKey = getIntent().getStringExtra(LIBRARY_ID);
        currentTextKey = getIntent().getStringExtra(TRANSLATION_ID);
        if (currentTextKey != null && libraryKey != null) {
            if (!(currentTextKey.equals("") && libraryKey.equals(""))) {
                FirebaseDBHelper.getInstance().readAndUpdateSingleText(libraryKey, currentTextKey);
            }
        }

        translationName = findViewById(R.id.edit_translation_act_translation_name);
        transLationDesc = findViewById(R.id.edit_translation_act_translation_desc);
        androidKey = findViewById(R.id.edit_translation_act_android_key);
        iosKey = findViewById(R.id.edit_translation_act_ios_key);
        webKey = findViewById(R.id.edit_translation_act_web_key);
        langSpinner = findViewById(R.id.edit_translation_act_language_dropdown);
        translationValue = findViewById(R.id.edit_translation_act_write_translation);
        submitTranslationButton = findViewById(R.id.edit_translation_act_submit_translation);

        Realm realm = RealmHelper.getInstance().getRealm();
        currentText = realm.where(LibraryData.class).equalTo(LIBRARY_ID, libraryKey).findFirst()
                .getTexts().where().equalTo(TRANSLATION_ID, currentTextKey).findFirst();

        if (currentTextKey != null) {
            updateUIWithText();
        } else {
            currentTextKey = UUID.randomUUID().toString();
        }

        putLanguagesIntoLists(realm, libraryKey);

        langSpinner.setOnItemSelectedListener(langSelectedListener);
        submitTranslationButton.setOnClickListener(buttonListener);
    }

    private void putLanguagesIntoLists(Realm realm, String libraryKey) {
        RealmList<LanguageData> languageDataRealmList = realm.where(LibraryData.class)
                .equalTo(LIBRARY_ID, libraryKey).findFirst().getLanguages();
        for (LanguageData language : languageDataRealmList) {
            langNames.add(language.getLangName());
            langKeys.add(language.getLangKey());
        }

        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this, R.layout.language_text_layout, R.id.language_text_view, langNames);
        langSpinner.setAdapter(langAdapter);
    }

    private final AdapterView.OnItemSelectedListener langSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (translationValue.getText().length() > 0) {
                translations.put(langKeys.get(langSpinner.getSelectedItemPosition()), translationValue.getText().toString());
            }
            translationValue.setText(translations.get(langKeys.get(langSpinner.getSelectedItemPosition())));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void updateUIWithText() {
        translationName.setText(currentText.getTranslationName());
        transLationDesc.setText(currentText.getTranslationDesc());
        androidKey.setText(currentText.getAndroidKey());
        iosKey.setText(currentText.getIosKey());
        webKey.setText(currentText.getWebKey());

        for (SingleTranslationData translationData : currentText.getTranslations()) {
            translations.put(translationData.getLangKey(), translationData.getTranslation());
        }
    }

    private final View.OnClickListener buttonListener = v -> {
        if (!checkIfRowsAreEmpty()) {
            saveCurrentText();
            clearAndCreateNew();
        }
    };


    @Override
    public void onBackPressed() {
        if (!checkIfRowsAreEmpty()) {
            super.onBackPressed();
            saveCurrentText();
        }
    }

    private void saveCurrentText() {
        Map<String, Object> textToSave = new HashMap<>();
        textToSave.put("name", translationName.getText().toString());
        textToSave.put("description", transLationDesc.getText().toString());
        textToSave.put("android_key", androidKey.getText().toString());
        textToSave.put("ios_key", iosKey.getText().toString());
        textToSave.put("web_key", webKey.getText().toString());
        textToSave.put("translations", translations);
        FirebaseDatabase.getInstance().getReference().child("libraries")
                .child(getIntent().getStringExtra(LIBRARY_ID)).child("texts")
                .child(currentTextKey).updateChildren(textToSave);
    }

    private boolean checkIfRowsAreEmpty() {
        if (translationName.getText().toString().isEmpty() || transLationDesc.getText().toString().isEmpty()) {
            popUpAlert.alertPopUp(EditTranslationActivity.this, R.string.pop_up_message_edit_translation_activity, "Warning");
            return true;
        }
        return false;
    }

    private void clearAndCreateNew() {
        translationName.setText("");
        transLationDesc.setText("");
        androidKey.setText("");
        iosKey.setText("");
        webKey.setText("");
        translationValue.setText("");
        translations.clear();

        currentTextKey = UUID.randomUUID().toString();
    }
}
