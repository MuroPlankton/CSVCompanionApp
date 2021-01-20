package com.choicely.csvcompanion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private static final String TAG = "LibraryAdapter";

    private final Context context;

    public LibraryAdapter(Context context){ this.context = context;}

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(context).inflate(R.layout.library_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        //TODO: add data from realm to here
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.library_list_row_library_name);
        }
    }
}
