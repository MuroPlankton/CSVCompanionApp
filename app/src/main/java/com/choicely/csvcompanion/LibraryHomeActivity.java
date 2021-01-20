package com.choicely.csvcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryHomeActivity extends AppCompatActivity {

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
    }
}
