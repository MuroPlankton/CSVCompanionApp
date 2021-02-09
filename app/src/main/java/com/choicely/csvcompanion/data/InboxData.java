package com.choicely.csvcompanion.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class InboxData extends RealmObject {

    @PrimaryKey
    private String libraryID;
    private String customMessage;
    private String senderID;

    public String getLibraryID() {
        return libraryID;
    }

    public void setLibraryID(String libraryID) {
        this.libraryID = libraryID;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

}
