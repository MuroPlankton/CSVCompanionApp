package com.choicely.csvcompanion.userProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private final List<InboxMessageData> list = new ArrayList<>();

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
        InboxMessageData message = list.get(position);
        holder.libraryID = message.getLibraryID();
        holder.libraryName = message.getLibraryName();

        String sender = message.getSenderName();
        String libraryName = message.getLibraryName();

        holder.content.setText(String.format("The user %s has sent you a library %s", sender, libraryName));
        holder.customMessage.setText(message.getCustomMessage());

        holder.decline.setOnClickListener(v -> {
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(InboxMessageData content) {
        list.add(content);
    }

    public void clear() {
        list.clear();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public static class UserProfileInboxViewHolder extends RecyclerView.ViewHolder {

        private final FirebaseDatabase database = FirebaseDatabase.getInstance();
        private final DatabaseReference ref = database.getReference();
        private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        public String libraryID;
        public String libraryName;
        public Context context;

        public TextView content;
        public TextView customMessage;
        public ImageButton decline;
        public ImageButton accept;

        public UserProfileInboxViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.inbox_text_view);
            customMessage = itemView.findViewById(R.id.inbox_text_view_custom_message);
            decline = itemView.findViewById(R.id.inbox_decline_image_button);
            accept = itemView.findViewById(R.id.inbox_accept_image_button);

//            decline.setOnClickListener(onClickListener);
            accept.setOnClickListener(onClickListener);
        }

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (v == decline) {
                    //TODO: might call the interface here and make the callback in onBindViewHolder
                } else*/
                if (v == accept) {
                    addSharedLibrary();
                }
            }
        };

        private void addSharedLibrary() {
            if (user != null) {
                DatabaseReference myRef = ref.child("user_libraries/" + user.getUid());
                Map<String, Object> map = new HashMap<>();
                map.put(libraryID, libraryName);
                myRef.updateChildren(map);

                DatabaseReference myRef2 = ref.child("libraries/" + libraryID + "/users");
                Map<String, Object> map2 = new HashMap<>();
                map2.put(user.getUid(), user.getDisplayName());
                myRef2.updateChildren(map2);

                Toast.makeText(itemView.getContext(), "Library has been added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface onItemRemovedListener {
        void onItemRemoved();
    }
}



