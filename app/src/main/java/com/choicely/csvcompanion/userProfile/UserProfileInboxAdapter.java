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
import com.choicely.csvcompanion.data.InboxMessageData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileInboxAdapter extends RecyclerView.Adapter<UserProfileInboxAdapter.UserProfileInboxViewHolder> {

    private final static String TAG = "InboxAdapter";
    private final Context context;
    private final List<InboxMessageData> itemList = new ArrayList<>();

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
        InboxMessageData message = itemList.get(position);
        holder.libraryID = message.getLibraryID();
        holder.libraryName = message.getLibraryName();

        String sender = message.getSenderName();
        String libraryID = message.getLibraryID();

        Log.d(TAG, "onBindViewHolder: sender: " + sender);

        holder.content.setText(String.format("The user %s has sent you a library %s", sender, libraryID));
        holder.customMessage.setText(message.getCustomMessage());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void add(InboxMessageData content) {
        itemList.add(content);
    }

    public void clear() {
        itemList.clear();
    }

    public static class UserProfileInboxViewHolder extends RecyclerView.ViewHolder {

        public String libraryID;
        public String libraryName;
        public TextView content;
        public TextView customMessage;

        public ImageButton close;
        public ImageButton check;

        private final FirebaseDatabase database = FirebaseDatabase.getInstance();
        private final DatabaseReference ref = database.getReference();
        private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        public UserProfileInboxViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.inbox_text_view);
            customMessage = itemView.findViewById(R.id.inbox_text_view_custom_message);
            close = itemView.findViewById(R.id.inbox_close_image_button);
            check = itemView.findViewById(R.id.inbox_check_image_button);

            close.setOnClickListener(onClickListener);
            check.setOnClickListener(onClickListener);
        }

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == close) {
                    Log.d(TAG, "Close clicked ");
                } else if (v == check) {

                    DatabaseReference myRef = ref.child("user_libraries/" + user.getUid());
                    Map<String, Object> map = new HashMap<>();
                    map.put(libraryID, libraryName);
                    myRef.updateChildren(map);

                    DatabaseReference myRef2 = ref.child("libraries/" + libraryID + "/users");
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put(user.getUid(), user.getDisplayName());
                    myRef2.updateChildren(map2);

                    Log.d(TAG, "Check clicked ");

                }
            }
        };

    }

}
