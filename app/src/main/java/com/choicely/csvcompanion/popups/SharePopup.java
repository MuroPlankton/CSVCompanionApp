package com.choicely.csvcompanion.popups;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.choicely.csvcompanion.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SharePopup extends Dialog {

    private static final String TAG = "SharePopup";
    EditText searchEditText;
    ListView searchUserSuggestionView;
    ArrayAdapter<String> userSuggestionAdapter;
    Timer userSearchTimer;

    public SharePopup(@NonNull Context context, String libraryID) {
        super(context);

        setContentView(R.layout.user_search_and_share_layout);
        searchEditText = findViewById(R.id.search_share_edit_text);
        searchEditText.addTextChangedListener(searchTextWatcher);

        searchUserSuggestionView = findViewById(R.id.search_share_list);
        userSuggestionAdapter = new ArrayAdapter<>(context, R.layout.language_text_layout, R.id.language_text_view);
        searchUserSuggestionView.setAdapter(userSuggestionAdapter);
        searchUserSuggestionView.setOnItemClickListener(foundUserItemClickListener);

        show();
    }

    private final TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (searchEditText.getText().length() > 2) {
                userSearchDelayer();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void userSearchDelayer() {
        TimerTask searchUsersTask = new TimerTask() {
            @Override
            public void run() {
                searchForUsers();
            }
        };

        if (userSearchTimer != null) {
            userSearchTimer.cancel();
            userSearchTimer.purge();
        }
        userSearchTimer = new Timer();
        userSearchTimer.schedule(searchUsersTask, 1000);
    }

    private void searchForUsers() {
        Query userSearchQuery = FirebaseDatabase.getInstance().getReference().
                child("users").orderByValue()
                .startAt(searchEditText.getText().toString())
                .endAt(searchEditText.getText().toString() + "\\uf8ff").limitToFirst(10);

        userSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: Users found. Here's the data: " + snapshot.toString());
                final Map<String, Object> matchingUsersMap = (Map<String, Object>) snapshot.getValue();
                setUserNamesToPopup(matchingUsersMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUserNamesToPopup(Map<String, Object> matchingUsersMap) {
        userSuggestionAdapter.clear();
        if (matchingUsersMap != null && !matchingUsersMap.isEmpty()) {
            for (Object userName : matchingUsersMap.values()) {
                userSuggestionAdapter.add(userName.toString());
            }
        }

        userSuggestionAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener foundUserItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
}
