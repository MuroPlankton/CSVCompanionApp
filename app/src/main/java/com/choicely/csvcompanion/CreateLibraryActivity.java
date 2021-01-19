package com.choicely.csvcompanion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateLibraryActivity extends AppCompatActivity {

    private Button createButton;
    private EditText nameField;
    private String libraryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_library_activity);

        createButton = findViewById(R.id.create_library_activity_create_button);
        nameField = findViewById(R.id.create_library_activity_edit_text);

    }

    public void onCreateButtonClicked(View view) {
        libraryName = nameField.getText().toString();
        Toast.makeText(this, "Library created with the name: " + libraryName, Toast.LENGTH_SHORT).show();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference usersRef = ref.child("users");

        Map<String, User> users = new HashMap<>();
        users.put("MuroPlankton", new User("Miro Planting"));
        users.put("ElRichardo", new User("Richard Nykänen"));

        usersRef.setValue(users);

    }

    public static class User {
        public String name;

        public User(String name) {
            this.name = name;
        }
    }

}
