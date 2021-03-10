package com.choicely.csvcompanion.userProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choicely.csvcompanion.R;
import com.choicely.csvcompanion.data.InboxMessageData;
import com.choicely.csvcompanion.db.RealmHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {

    private final static String TAG = "InboxAdapter";
    private final Context context;
    private final List<InboxMessageData> list = new ArrayList<>();

    private InboxViewHolder inboxViewHolder;

    public InboxAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inboxViewHolder = new InboxViewHolder(LayoutInflater.from(context).inflate(R.layout.inbox_list_row, parent, false));
        return inboxViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        InboxMessageData message = list.get(position);
        holder.libraryID = message.getLibraryID();
        holder.libraryName = message.getLibraryName();

        String sender = message.getSenderName();
        String libraryName = message.getLibraryName();

        holder.content.setText(String.format("The user '%s' has sent you a library '%s'", sender, libraryName));
        holder.customMessage.setText(message.getCustomMessage());

        InboxViewHolder.OnItemRemovedListener onItemRemovedListener = () -> remove(position, holder.libraryID);

//        InboxViewHolder viewHolder = new InboxViewHolder(holder.itemView);
        inboxViewHolder.setItemRemovedListener(onItemRemovedListener);
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

    public void remove(int position, String libraryID) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        RealmHelper helper = RealmHelper.getInstance();
        Realm realm = helper.getRealm();

        realm.executeTransaction(realm1 -> {
            InboxMessageData message = realm.where(InboxMessageData.class).equalTo("libraryID", libraryID).findFirst();
            message.deleteFromRealm();
        });

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference inboxMessage = ref.child("user_inbox").child(currentUser).child(libraryID);
        inboxMessage.removeValue();
    }

    public static class InboxViewHolder extends RecyclerView.ViewHolder {

        private final FirebaseDatabase database = FirebaseDatabase.getInstance();
        private final DatabaseReference ref = database.getReference();
        private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        public OnItemRemovedListener listener;

        public String libraryID;
        public String libraryName;
        public Context context;
        public TextView content;

        public TextView customMessage;
        public ImageButton decline;
        public ImageButton accept;

        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.inbox_list_row_title);
            customMessage = itemView.findViewById(R.id.inbox_list_row_custom_message);
            decline = itemView.findViewById(R.id.inbox_list_row_decline_image_button);
            accept = itemView.findViewById(R.id.inbox_list_row_accept_image_button);

            accept.setOnClickListener(onClickListener);
            decline.setOnClickListener(onClickListener);
        }

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == accept) {
                    addSharedLibrary();
                    if (listener != null) {
                        listener.onItemRemoved();
                    }
                } else if (v == decline) {
                    if (listener != null) {
                        listener.onItemRemoved();
                    }
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

        public void setItemRemovedListener(OnItemRemovedListener listener) {
            this.listener = listener;
        }

        public interface OnItemRemovedListener {
            void onItemRemoved();
        }
    }
}



