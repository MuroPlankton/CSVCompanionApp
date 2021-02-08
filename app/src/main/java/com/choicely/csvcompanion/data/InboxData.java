package com.choicely.csvcompanion.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class InboxData extends RealmObject {

    @PrimaryKey
    private String messageID;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
