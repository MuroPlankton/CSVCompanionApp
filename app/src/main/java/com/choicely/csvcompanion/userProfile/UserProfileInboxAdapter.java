package com.choicely.csvcompanion.userProfile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.InboxData;

import java.util.ArrayList;
import java.util.List;

public class UserProfileInboxAdapter extends RecyclerView.Adapter<UserProfileInboxAdapter.UserProfileInboxViewHolder> {

    private final static String TAG = "InboxAdapter";
    private final Context context;
    private final List<InboxData> itemList = new ArrayList<>();

    public UserProfileInboxAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UserProfileInboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserProfileInboxViewHolder(LayoutInflater.from(context).inflate(R.layout.inbox_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileInboxViewHolder holder, int position) {
        InboxData content = itemList.get(position);

        holder.content.setText(content.getMessage());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void add(InboxData content) {
        itemList.add(content);
    }

    public void clear() {
        itemList.clear();
    }

    public static class UserProfileInboxViewHolder extends RecyclerView.ViewHolder {

        String contentID;
        public TextView content;
        public ImageButton close;
        public ImageButton check;

        public UserProfileInboxViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.inbox_text_view);
            close = itemView.findViewById(R.id.inbox_close_image_button);
            check = itemView.findViewById(R.id.inbox_check_image_button);

            close.setOnClickListener(onClickListener);
            check.setOnClickListener(onClickListener);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == close) {
                    Log.d(TAG, "Close clicked ");
                } else {
                    Log.d(TAG, "Check clicked ");
                }
            }
        };

    }

}
