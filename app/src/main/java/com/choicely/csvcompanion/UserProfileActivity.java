package com.choicely.csvcompanion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private Button saveChangesButton;
    private EditText userNameEditText;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private PopUpAlert popUpAlert = new PopUpAlert();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        saveChangesButton = findViewById(R.id.user_profile_activity_save_changes_button);
        userNameEditText = findViewById(R.id.user_profile_activity_user_name_edit_text);

        setUserNameToEditText();
    }

    private void setUserNameToEditText() {
        userNameEditText.setText(user.getDisplayName());
    }

    public void onClick(View view) {
        if (view == saveChangesButton) {
            saveChanges();
        }
    }

    private void saveChanges() {
        DatabaseReference myRef = ref.child("users/" + user.getUid());

        String newUserName = userNameEditText.getText().toString();

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUserName)
                .build();

        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> userNameMap = new HashMap<>();
                        userNameMap.put("name", newUserName);
                        myRef.updateChildren(userNameMap);

                        Toast.makeText(UserProfileActivity.this, "Username changed to " + newUserName, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
