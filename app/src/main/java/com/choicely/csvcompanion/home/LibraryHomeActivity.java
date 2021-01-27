package com.choicely.csvcompanion.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.library_content.LibraryActivity;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;

import io.realm.Realm;
import io.realm.RealmResults;

public class LibraryHomeActivity extends AppCompatActivity {

    private static final String TAG = "LibraryHomeActivity";

    private Button newLibraryButton;
    private ImageButton searchButton;
    private ImageButton backSpace;
    private EditText searchField;
    private TextView heading;
    private boolean isSearchActive = false;
    private RecyclerView libraryRecycler;
    private LibraryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_home_activity);

        newLibraryButton = findViewById(R.id.activity_library_home_new_library);
        searchButton = findViewById(R.id.library_home_activity_search_button);
        searchField = findViewById(R.id.library_home_activity_search_field);
        backSpace = findViewById(R.id.library_home_activity_backspace);
        heading = findViewById(R.id.library_home_activity_heading);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearchedContent();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        libraryRecycler = findViewById(R.id.library_home_activity_recycler);
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryAdapter(this);
        libraryRecycler.setAdapter(adapter);

        startFireBaseListening();
    }

    public void onClick(View v) {
        if (v == searchButton) {
            isSearchActive = true;
            searchActivity();
        } else if (v == backSpace) {
            searchField.getText().clear();
            isSearchActive = false;
            searchActivity();
        } else if (v == newLibraryButton) {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
        }
    }

    private void updateSearchedContent() {
        adapter.clear();

        RealmHelper helper = RealmHelper.getInstance();
        Realm realm = helper.getRealm();

        RealmResults<LibraryData> libraryNames = realm.where(LibraryData.class).contains("libraryName", searchField.getText().toString()).findAll();

        for (LibraryData libraryName : libraryNames) {
            adapter.add(libraryName);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFireBaseListening();
    }

    private void startFireBaseListening() {
        FirebaseDBHelper helper = FirebaseDBHelper.getInstance();
        helper.setListener(this::updateContent);
        helper.listenForLibraryDataChange();
    }

    private void updateContent() {
        adapter.clear();

        RealmHelper helper = RealmHelper.getInstance();
        Realm realm = helper.getRealm();

        RealmResults<LibraryData> libraries = realm.where(LibraryData.class).findAll();

        for (LibraryData library : libraries) {
            adapter.add(library);
        }

        adapter.notifyDataSetChanged();
    }

    private void searchActivity() {
        if (isSearchActive) {
            searchField.setVisibility(View.VISIBLE);
            backSpace.setVisibility(View.VISIBLE);
            heading.setVisibility(View.INVISIBLE);
        } else {
            searchField.setVisibility(View.GONE);
            backSpace.setVisibility(View.GONE);
            heading.setVisibility(View.VISIBLE);
        }
    }
}