package com.choicely.csvcompanion.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.LibraryActivity;
import com.choicely.csvcompanion.LibraryData;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.choicely.csvcompanion.home.LibraryAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

public class LibraryHomeActivity extends AppCompatActivity {

    private static final String TAG = "LibraryHomeActivity";

    private RecyclerView libraryRecycler;
    private Button newLibraryButton;
    private LibraryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_home_activity);

        newLibraryButton = findViewById(R.id.activity_library_home_new_library);

        newLibraryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
        });

        libraryRecycler = findViewById(R.id.library_home_activity_recycler);
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryAdapter(this);
        libraryRecycler.setAdapter(adapter);

        startFireBaseListening();
//        updateContent();
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

        for(LibraryData library : libraries){
            adapter.add(library);
        }

        adapter.notifyDataSetChanged();
    }
}