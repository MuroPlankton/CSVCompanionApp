package com.choicely.csvcompanion.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.IntentKeys;
import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.LibraryData;
import com.choicely.csvcompanion.libraryContent.LibraryActivity;
import com.choicely.csvcompanion.popups.SharePopup;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private static final String TAG = "LibraryAdapter";
    private final Context context;
    private final List<LibraryData> list = new ArrayList<>();
    private Activity mainActivity;

    public LibraryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(context).inflate(R.layout.library_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        LibraryData library = list.get(position);

        holder.libraryID = library.getLibraryID();
        holder.activity = mainActivity;

        if (library.getLibraryName() != null) {
            if (library.getLibraryName().isEmpty()) {
                holder.libraryName.setHint("library has no name...");
            } else {
                holder.libraryName.setText(library.getLibraryName());
            }
        }
    }

    public void add(LibraryData library) {
        list.add(library);
    }

    public void addActivity(Activity activity) {
        this.mainActivity = activity;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {

        public String libraryID;
        public TextView libraryName;
        public ImageButton shareButton;
        public Activity activity;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(onRowClick);

            libraryName = itemView.findViewById(R.id.library_list_row_library_name);
            shareButton = itemView.findViewById(R.id.library_list_row_share_button);
            shareButton.setOnClickListener(onShareButtonClicked);
        }

        private final View.OnClickListener onShareButtonClicked = view -> {
            new SharePopup(libraryName.getContext(), libraryID, activity);
        };

        private final View.OnClickListener onRowClick = view -> {
            Context ctx = libraryName.getContext();
            Intent intent = new Intent(ctx, LibraryActivity.class);
            intent.putExtra(IntentKeys.LIBRARY_ID, libraryID);
            ctx.startActivity(intent);
        };
    }
}
