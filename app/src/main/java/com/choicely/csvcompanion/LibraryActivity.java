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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LibraryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryProfileAct";

    private EditText libraryName;
    private List<String> langList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_profile);
        libraryName = findViewById(R.id.activity_library_profile_name);

        langList = new ArrayList<>();
        langList.add("en");
        langList.add("fi");
        langList.add("it");
        langList.add("sv");

        createNewLibrary();
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private DatabaseReference librariesRef = ref.child("libraries/Library1");


    public void createNewLibrary() {
//        newChild();
//        updateChild();
        addLanguage();

//        librariesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d(TAG, "onDataChange: things have changed " + snapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
    }

    private void addLanguage() {
        DatabaseReference librariesRef = ref.child("libraries/Library1/languages");

        Map<String, Object> langMap = new HashMap<>();

        for(int i = 0; i < langList.size(); i++){
            langMap.put(UUID.randomUUID().toString(), langList.get(i));
        }

        librariesRef.setValue(langMap);

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
