package com.choicely.csvcompanion.library_content;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.EditTranslationActivity;
import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.data.TextData;

import java.util.ArrayList;
import java.util.List;

public class LibraryContentAdapter extends RecyclerView.Adapter<LibraryContentAdapter.LibraryContentViewHolder> {

    private static final String TAG = "ContentAdapter";
    private final Context context;
    private LibraryData libraryData;

    List<String> textNameList = new ArrayList<>();
    List<String> textDescList = new ArrayList<>();
    List<String> textIDList = new ArrayList<>();

    public LibraryContentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LibraryContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryContentViewHolder(LayoutInflater.from(context).inflate(R.layout.library_content_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryContentViewHolder holder, int position) {
        holder.libraryID = libraryData.getLibraryID();

        holder.textID = textIDList.get(position);

        holder.desc.setText(textDescList.get(position));
        holder.textName.setText(textNameList.get(position));

        Log.d(TAG, "onBindViewHolder: " + position);
    }

    public void add(String id, String name, String desc){
        textIDList.add(id);
        textNameList.add(name);
        textDescList.add(desc);
    }

    public void setLibrary(LibraryData library){
        this.libraryData = library;
    }

    @Override
    public int getItemCount() {
        return textIDList.size();
    }

    public void clear() {
        textDescList.clear();
        textIDList.clear();
        textNameList.clear();
    }

    public static class LibraryContentViewHolder extends RecyclerView.ViewHolder {

        String textID;
        String libraryID;
        public TextView desc;
        public TextView textName;


        public LibraryContentViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(onRowClick);
            textName = itemView.findViewById(R.id.content_row_translation_name);
            desc = itemView.findViewById(R.id.content_row_translation_description);
        }

        private final View.OnClickListener onRowClick = view -> {

            Context ctx = desc.getContext();
            Intent intent = new Intent(ctx, EditTranslationActivity.class);
            intent.putExtra(IntentKeys.LIBRARY_ID, libraryID);
            intent.putExtra(IntentKeys.TRANSLATION_ID, textID);
            ctx.startActivity(intent);
        };
    }
}
