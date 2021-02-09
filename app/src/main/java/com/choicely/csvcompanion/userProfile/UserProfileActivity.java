package com.choicely.csvcompanion.userProfile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.InboxData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.choicely.csvcompanion.popups.PopUpAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class UserProfileActivity extends AppCompatActivity {

    private final static String TAG = "UserProfileActivity";

    private Button saveChangesButton;
    private Button signOutButton;
    private EditText userNameEditText;

    private RecyclerView inboxRecyclerView;
    private UserProfileInboxAdapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final PopUpAlert popUpAlert = new PopUpAlert();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        saveChangesButton = findViewById(R.id.user_profile_activity_save_changes_button);
        signOutButton = findViewById(R.id.user_profile_activity_sign_out_button);
        userNameEditText = findViewById(R.id.user_profile_activity_user_name_edit_text);
        setUserNameToEditText();

        inboxRecyclerView = findViewById(R.id.user_profile_recycler_view);
        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserProfileInboxAdapter(this);
        inboxRecyclerView.setAdapter(adapter);

        startFireBaseListening();
        updateContent();


    }

    private void startFireBaseListening() {
        FirebaseDBHelper helper = FirebaseDBHelper.getInstance();
        helper.setListener(this::updateContent);
        helper.listenForUserInboxDataChange();
    }

    private void updateContent() {
        adapter.clear();

        RealmHelper helper = RealmHelper.getInstance();
        Realm realm = helper.getRealm();

        RealmResults<InboxData> inboxContent = realm.where(InboxData.class).findAll();

        for (InboxData content : inboxContent) {
            adapter.add(content);
        }
        adapter.notifyDataSetChanged();
    }

    private void setUserNameToEditText() {
        userNameEditText.setText(user.getDisplayName());
    }

    public void onClick(View view) {
        if (view == saveChangesButton) {
            saveChanges();
        } else if (view == signOutButton) {
            signUserOut();
        }
    }

    private void signUserOut() {
        popUpAlert.askForUserValidation(this, R.string.pop_up_message_user_profile_activity, "sign out?");
    }

    private void saveChanges() {
        DatabaseReference myRef = ref.child("users");

        String newUserName = userNameEditText.getText().toString();

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUserName)
                .build();

        user.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> userNameMap = new HashMap<>();
                userNameMap.put(user.getUid(), newUserName);
                myRef.updateChildren(userNameMap);

                Toast.makeText(UserProfileActivity.this, "Username changed to " + newUserName, Toast.LENGTH_SHORT).show();
                updateUserNamesInAllLibraries();
            }
        });
    }

    private void updateUserNamesInAllLibraries() {
        ArrayList<String> libraryIDArrayList = getIntent().getStringArrayListExtra(IntentKeys.LIBRARY_LIST_ID);

        for (int i = 0; i < libraryIDArrayList.size(); i++) {
            String id = libraryIDArrayList.get(i);
            DatabaseReference myRef = ref.child("libraries/" + id + "/users");

            Map<String, Object> userMap = new HashMap<>();
            userMap.put(user.getUid(), user.getDisplayName());
            myRef.updateChildren(userMap);
        }
    }
}
