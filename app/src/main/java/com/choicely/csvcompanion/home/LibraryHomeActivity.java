package com.choicely.csvcompanion.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.choicely.csvcompanion.libraryContent.LibraryActivity;

import io.realm.Realm;
import io.realm.RealmResults;

public class LibraryHomeActivity extends AppCompatActivity {

    private static final String TAG = "LibraryHomeActivity";

    private Button newLibraryButton;
    private SearchView searchView;
    private TextView heading;
    private RecyclerView libraryRecycler;
    private LibraryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_home_activity);

        newLibraryButton = findViewById(R.id.activity_library_home_new_library);
        heading = findViewById(R.id.library_home_activity_heading);

        libraryRecycler = findViewById(R.id.library_home_activity_recycler);
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryAdapter(this);
        libraryRecycler.setAdapter(adapter);

        startFireBaseListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library_home_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_libraries);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearchedContent();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFireBaseListening();
    }

    public void onClick(View v) {
        if (v == newLibraryButton) {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
        }
    }

    private void updateSearchedContent() {
        adapter.clear();

        RealmHelper helper = RealmHelper.getInstance();
        Realm realm = helper.getRealm();

        RealmResults<LibraryData> libraryNames = realm.where(LibraryData.class).contains("libraryName", searchView.getQuery().toString()).findAll();

        for (LibraryData libraryName : libraryNames) {
            adapter.add(libraryName);
        }

        adapter.notifyDataSetChanged();
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
}