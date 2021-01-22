package com.choicely.csvcompanion.content;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.EditTranslationActivity;
import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;

import java.util.ArrayList;
import java.util.List;

public class LibraryContentAdapter extends RecyclerView.Adapter<LibraryContentAdapter.LibraryContentViewHolder> {

    private final List<String> translationList = new ArrayList<>();
    private final Context context;

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

        String string = translationList.get(position);

        holder.translation.setText(string);
    }

    @Override
    public int getItemCount() {
        return translationList.size();
    }

    public void add(String translation) {
        translationList.add(translation);
    }

    public void clear() {
        translationList.clear();
    }

    public static class LibraryContentViewHolder extends RecyclerView.ViewHolder {

        long translationID;
        public TextView translation;

        public LibraryContentViewHolder(@NonNull View itemView) {
            super(itemView);
//            itemView.setOnClickListener(onRowClick);

            translation = itemView.findViewById(R.id.content_row);
        }

        private final View.OnClickListener onRowClick = view -> {

            Context ctx = translation.getContext();
            Intent intent = new Intent(ctx, EditTranslationActivity.class);
            intent.putExtra(IntentKeys.TRANSLATION_ID, translationID);
            ctx.startActivity(intent);
        };
    }
}
