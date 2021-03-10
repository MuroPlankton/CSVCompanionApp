package com.choicely.csvcompanion.popups;

import android.app.Activity;
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
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class SharePopup extends Dialog {

    private static final String TAG = "SharePopup";
    EditText searchEditText;
    ListView searchUserSuggestionView;
    ArrayAdapter<String> userSuggestionAdapter;
    Timer userSearchTimer;

    private final Activity activity;
    private final String libraryID;
    private String userID;


    public SharePopup(@NonNull Context context, String libraryID, Activity activity) {
        super(context);

        this.activity = activity;
        this.libraryID = libraryID;

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
        userSearchTimer.schedule(searchUsersTask, 500);
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

                try {
                    userID = matchingUsersMap.keySet().toString();
                    userID = userID.substring(1, userID.length() - 1);

                    Log.d(TAG, "onDataChange: userID:  " + userID);
                } catch (NullPointerException e) {
                    Log.e(TAG, "onDataChange: " + e.getMessage());
                }
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

    private final PopUpAlert popUpAlert = new PopUpAlert();

    private final AdapterView.OnItemClickListener foundUserItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: userID: " + userID);
            popUpAlert.shareLibraryPopUp(activity, R.string.pop_up_message_share_pop_up, "Sharing is caring",
                    userID, getLibraryName(), libraryID);
        }
    };

    private String getLibraryName() {
        Realm realm = RealmHelper.getInstance().getRealm();
        LibraryData libraryData = realm.where(LibraryData.class).equalTo("libraryID", libraryID).findFirst();

        String libraryName = libraryData.getLibraryName();
        Log.d(TAG, "getLibraryName: " + libraryName);
        return libraryName;

    }
}
