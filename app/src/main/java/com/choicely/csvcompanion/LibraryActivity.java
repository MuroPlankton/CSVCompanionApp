package com.choicely.csvcompanion;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LibraryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryProfileAct";

    private EditText libraryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_profile);
        libraryName = findViewById(R.id.activity_library_profile_name);

        createNewLibrary();
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private DatabaseReference librariesRef = ref.child("libraries/Library1");

    public void createNewLibrary() {
//        newChild();
//        updateChild();
        addLanguage();

        librariesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: things have changed");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addLanguage() {
        Map<String, Object> map = new HashMap<>();
        map.put("Language1", new Languages("fi", "suomi"));
        map.put("Language2", new Languages("en", "english"));
        map.put("Language3", new Languages("sv", "svenska"));
        librariesRef.updateChildren(map);
    }

    private void updateChild() {
        DatabaseReference updateRef = librariesRef.child("Language");
        Map<String, Object> libUpdates = new HashMap<>();
        libUpdates.put("language", "en");
        updateRef.updateChildren(libUpdates);
    }

    private void newChild() {
        Map<String, Library> map = new HashMap<>();
        map.put("Library1", new Library("Choicely germany"));
        librariesRef.push().setValue(map);
    }
}
