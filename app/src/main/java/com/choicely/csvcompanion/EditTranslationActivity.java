package com.choicely.csvcompanion;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditTranslationActivity extends AppCompatActivity {

    private Button anotherTranslationButton;
    private EditText translationName, transLationDesc;
    private EditText androidKey, iosKey, webKey;
    private Spinner langSpinner;
    private EditText translationValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.edit_translation_activity);

        
    }
}
