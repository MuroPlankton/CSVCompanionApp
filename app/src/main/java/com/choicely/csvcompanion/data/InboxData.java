package com.choicely.csvcompanion.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class InboxData extends RealmObject {

    @PrimaryKey
    private String messageID;
    private String messageContent;

    public String getMessageID() { return messageID; }

    public void setMessageID(String messageID) { this.messageID = messageID; }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
