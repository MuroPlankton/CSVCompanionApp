package com.choicely.csvcompanion.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import io.realm.Realm;

public class FirebaseDBHelper {

    private static final String TAG = "FirebaseDBHelper";

    private static FirebaseDBHelper instance;

    private FirebaseDBHelper(){

    }

    public static void init(){
        if(instance != null){
            throw new IllegalStateException(TAG + " is already initialized!");
        }

        instance = new FirebaseDBHelper();
    }

    public static FirebaseDBHelper getInstance(){
        if(instance == null){
            throw new IllegalStateException(TAG + " is not initialized!");
        }

        return instance;
    }

    public void listenForLibraryDataChange(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("libraries");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Object changedData = snapshot.getValue();
                readFirebaseLibraries(changedData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    public void readFirebaseLibraries(Object libraries){
        if(libraries instanceof Map){
            final Map<String, Object> librariesMap = (Map<String, Object>) libraries;

            RealmHelper helper = RealmHelper.getInstance();
            Realm realm = helper.getRealm();

            realm.executeTransaction(realm1 -> {
                for(String key : librariesMap.keySet()){

                    Object libraryObject = librariesMap.get(key);
                    final Map<String, Object> libraryMap = (Map<String, Object>) libraryObject;

                    if(libraryMap != null) {
                        libraryMap.get("libraryName");
                    }
                }
            });
        }
    }


}
