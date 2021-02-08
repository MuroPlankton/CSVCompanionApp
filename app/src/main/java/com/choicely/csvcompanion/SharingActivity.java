package com.choicely.csvcompanion;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SharingActivity extends AppCompatActivity {

    private Button button;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharing_activity);

        button = findViewById(R.id.send_library_to_jarno);

        button.setOnClickListener(v -> {
            DatabaseReference myRef = ref.child("user_libraries/" + user.getUid());

            Map<String, Object> map = new HashMap<>();
            map.put(UUID.randomUUID().toString(), "71823bbd-ab9a-4397-b62e-479dd18e79bd");
            myRef.updateChildren(map);

        });
    }
}
