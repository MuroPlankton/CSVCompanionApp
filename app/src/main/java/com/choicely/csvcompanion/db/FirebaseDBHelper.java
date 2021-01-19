package com.choicely.csvcompanion.db;

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


}
