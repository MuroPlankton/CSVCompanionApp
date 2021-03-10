package com.choicely.csvcompanion;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.choicely.csvcompanion.data.LanguageData;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.SingleTranslationData;
import com.choicely.csvcompanion.data.TextData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.choicely.csvcompanion.popups.PopUpAlert;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.realm.RealmList;

import static com.choicely.csvcompanion.IntentKeys.LIBRARY_ID;
import static com.choicely.csvcompanion.IntentKeys.TRANSLATION_ID;

public class EditTranslationActivity extends AppCompatActivity {

    private final PopUpAlert popUpAlert = new PopUpAlert();
    private static final String TAG = "EditTranslationActivity";

    private EditText translationName, translationDesc;
    private EditText androidKey, iosKey, webAdminKey, webMainKey, webWidgetKey;
    private Spinner langSpinner;
    private EditText translationValue;

    private String currentLibraryKey;
    private String currentTextKey;
    private TextData currentText;
    private final List<String> langKeys = new ArrayList<>();
    private final List<String> langNames = new ArrayList<>();
    private Map<String, String> translations = new HashMap<>();
    private String currentSelectedLang;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_translation_activity);

        currentLibraryKey = getIntent().getStringExtra(LIBRARY_ID);
        currentTextKey = getIntent().getStringExtra(TRANSLATION_ID);
        if (currentTextKey != null && currentLibraryKey != null) {
            if (!(currentTextKey.equals("") && currentLibraryKey.equals(""))) {
                FirebaseDBHelper.getInstance().readAndUpdateSingleText(currentLibraryKey, currentTextKey);
            }
        }

        translationName = findViewById(R.id.edit_translation_act_translation_name);
        translationDesc = findViewById(R.id.edit_translation_act_translation_desc);
        androidKey = findViewById(R.id.edit_translation_act_android_key);
        iosKey = findViewById(R.id.edit_translation_act_ios_key);
        webAdminKey = findViewById(R.id.edit_translation_act_web_admin_key);
        webMainKey = findViewById(R.id.edit_translation_act_web_main_key);
        webWidgetKey = findViewById(R.id.edit_translation_act_web_widget_key);
        langSpinner = findViewById(R.id.edit_translation_act_language_dropdown);
        translationValue = findViewById(R.id.edit_translation_act_write_translation);

        if (currentTextKey != null) {
            FirebaseDBHelper.getInstance().setTextLoadListener(textLoadedListener);
        } else {
            currentTextKey = UUID.randomUUID().toString();
        }

        putLanguagesIntoLists();

        translationValue.setText(translations.get(langKeys.get(langSpinner.getSelectedItemPosition())));
        langSpinner.setOnItemSelectedListener(langSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_translation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_translation_menu_save) {
            saveCurrentText();
            Toast.makeText(this, "Translation saved", Toast.LENGTH_SHORT).show();
            clearAndCreateNew();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void putLanguagesIntoLists() {
        RealmList<LanguageData> languageDataRealmList = RealmHelper.getInstance().getRealm()
                .where(LibraryData.class).equalTo("libraryID", currentLibraryKey).findFirst().getLanguages();

        for (LanguageData language : languageDataRealmList) {
            langNames.add(language.getLangName());
            langKeys.add(language.getLangKey());
        }

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this, R.layout.language_text_layout, R.id.language_text_view, langNames);
        langSpinner.setAdapter(langAdapter);
    }

    private final AdapterView.OnItemSelectedListener langSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (translationValue.getText().length() > 0) {
                translations.put(currentSelectedLang, translationValue.getText().toString());
            }
            translationValue.setText(translations.get(langKeys.get(langSpinner.getSelectedItemPosition())));
            currentSelectedLang = langKeys.get(langSpinner.getSelectedItemPosition());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private FirebaseDBHelper.onSingleTextLoadedListener textLoadedListener = this::loadText;

    private void loadText() {
        currentText = RealmHelper.getInstance().getRealm()
                .where(LibraryData.class).equalTo("libraryID", currentLibraryKey).findFirst()
                .getTexts().where().equalTo("textKey", currentTextKey).findFirst();

        translationName.setText(currentText.getTranslationName());
        translationDesc.setText(currentText.getTranslationDesc());
        androidKey.setText(currentText.getAndroidKey());
        iosKey.setText(currentText.getIosKey());
        webAdminKey.setText(currentText.getWebAdminKey());
        webMainKey.setText(currentText.getWebMainKey());
        webWidgetKey.setText(currentText.getWebWidgetKey());

        Log.d(TAG, "current text: " + currentText);

        for (SingleTranslationData translationData : currentText.getTranslations()) {
            translations.put(translationData.getLangKey(), translationData.getTranslation());
        }
    }

    @Override
    public void onBackPressed() {
        if (!checkIfRowsAreEmpty()) {
            super.onBackPressed();
            saveCurrentText();
            FirebaseDBHelper.getInstance().readAndUpdateSingleText(currentLibraryKey, currentTextKey);
        }
    }

    private void saveCurrentText() {
        Map<String, Object> textToSave = new HashMap<>();

        textToSave.put("name", translationName.getText().toString());
        textToSave.put("description", translationDesc.getText().toString());
        textToSave.put("android_key", androidKey.getText().toString());
        textToSave.put("ios_key", iosKey.getText().toString());
        textToSave.put("web_admin_key", webAdminKey.getText().toString());
        textToSave.put("web_key", webMainKey.getText().toString());
        textToSave.put("web_widget_key", webWidgetKey.getText().toString());
        translations.put(currentSelectedLang, translationValue.getText().toString());
        textToSave.put("translations", translations);

        FirebaseDatabase.getInstance().getReference()
                .child("libraries")
                .child(getIntent().getStringExtra(LIBRARY_ID))
                .child("texts")
                .child(currentTextKey)
                .updateChildren(textToSave);
    }

    private boolean checkIfRowsAreEmpty() {

        if (translationName.getText().toString().isEmpty()
                || translationDesc.getText().toString().isEmpty()
                || hasAtLeastOneKey() || translations.size() < 1) {

            popUpAlert.alertPopUp(EditTranslationActivity.this,
                    R.string.pop_up_message_edit_translation_activity, "Warning",
                    getResources().getString(R.string.dont_save_popup_text),
                    getResources().getString(R.string.continue_editing_popup_text));

            return true;
        }
        return false;
    }

    private boolean hasAtLeastOneKey() {
        List<String> keyFieldStrings = new ArrayList<>();
        keyFieldStrings.add(androidKey.getText().toString());
        keyFieldStrings.add(iosKey.getText().toString());
        keyFieldStrings.add(webMainKey.getText().toString());
        keyFieldStrings.add(webWidgetKey.getText().toString());
        keyFieldStrings.add(webAdminKey.getText().toString());
        for (String platformKey : keyFieldStrings) {
            if (!platformKey.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void clearAndCreateNew() {
        translationName.getText().clear();
        translationDesc.getText().clear();
        androidKey.getText().clear();
        iosKey.getText().clear();
        webAdminKey.getText().clear();
        webMainKey.getText().clear();
        webWidgetKey.getText().clear();
        translationValue.getText().clear();
        translations.clear();

        currentTextKey = UUID.randomUUID().toString();
    }
}
