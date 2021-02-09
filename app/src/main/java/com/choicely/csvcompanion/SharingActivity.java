package com.choicely.csvcompanion;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class SharingActivity extends AppCompatActivity {

    private Button button;
    private EditText customMessageEditText;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String libraryID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharing_activity);

        button = findViewById(R.id.send_library_to_jarno);
        customMessageEditText = findViewById(R.id.sharing_activity_custom_message_edit_text);

        libraryID = getIntent().getStringExtra(IntentKeys.LIBRARY_ID);

        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        button.setOnClickListener(v -> {
            DatabaseReference myRef = ref.child("user_inbox/feHvfGJ3Iwc8D565wQU7GHnH5hu2"); //7TEd1NfrdxfyvVXhYB7FKmL6s5t1   <-  riku

            String customMessage = customMessageEditText.getText().toString();

            Map<String, Object> sharedLibrary = new HashMap<>();
            Map<String, Object> sharedLibraryContent = new HashMap<>();

            if(libraryData != null) {
                sharedLibraryContent.put("library_name", libraryData.getLibraryName());
                sharedLibraryContent.put("custom_message", customMessage);
                sharedLibraryContent.put("sender_name", user.getDisplayName());

                sharedLibrary.put(libraryID, sharedLibraryContent);

                myRef.updateChildren(sharedLibrary);
            }
        });
    }
}
