package com.choicely.csvcompanion.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.FireBaseMessagingClass;
import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.SharingActivity;
import com.choicely.csvcompanion.UserProfileActivity;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.db.FirebaseDBHelper;
import com.choicely.csvcompanion.db.RealmHelper;
import com.choicely.csvcompanion.libraryContent.LibraryActivity;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button newLibraryButton;
    private SearchView searchView;
    private RecyclerView libraryRecycler;
    private LibraryAdapter adapter;

    private ImageButton shareLibraryButton;
    private FireBaseMessagingClass fireBaseMessagingClass = new FireBaseMessagingClass();
    private ArrayList<String> libraryIdList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Log.d(TAG, "onCreate: " + FirebaseInstallations.getInstance().getId());
        newLibraryButton = findViewById(R.id.main_activity_new_library);
        shareLibraryButton = findViewById(R.id.library_list_row_share_button);

        newLibraryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
        });
        libraryRecycler = findViewById(R.id.main_activity_recycler);
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryAdapter(this);
        libraryRecycler.setAdapter(adapter);

        fireBaseMessagingClass.getFCMToken();
        fireBaseMessagingClass.subscribeToTopic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFireBaseListening();
    }

    private void startFireBaseListening() {
        FirebaseDBHelper helper = FirebaseDBHelper.getInstance();
        helper.setListener(this::updateContent);
        helper.listenForUserLibraryDataChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

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

        MenuItem profileItem = menu.findItem(R.id.action_user_profile);
        profileItem.setOnMenuItemClickListener(item -> {
            getLibraryIDs();

            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            intent.putStringArrayListExtra(IntentKeys.LIBRARY_LIST_ID, libraryIdList);
            startActivity(intent);

            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void getLibraryIDs() {
        RealmHelper helper = RealmHelper.getInstance();
        Realm realm = helper.getRealm();

        RealmResults<LibraryData> libraries = realm.where(LibraryData.class).findAll();

        for (LibraryData library : libraries) {
            libraryIdList.add(library.getLibraryID());
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


    private void updateContent() {
        adapter.clear();

        RealmHelper helper = RealmHelper.getInstance();
        Realm realm = helper.getRealm();

        RealmResults<LibraryData> libraries = realm.where(LibraryData.class).findAll();

        for (LibraryData library : libraries) {
            adapter.add(library);
            Log.w(TAG, "object added to adapter: " + library);
        }

        adapter.notifyDataSetChanged();
        Log.w(TAG, "adapter count: " + adapter.getItemCount());
    }
}