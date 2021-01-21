package com.choicely.csvcompanion;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LibraryData extends RealmObject {

    @PrimaryKey
    private int libraryID;
    private String libraryName;

    public int getLibraryID() { return libraryID; }

    public void setLibraryID(int libraryID) { this.libraryID = libraryID; }

    public String getLibraryName() { return libraryName; }

    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }
}
