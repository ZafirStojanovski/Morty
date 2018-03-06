package com.zafirstojanovski.morty.Loaders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zafir Stojanovski on 3/5/2018.
 */

class FlaskMessageResponse {

    @SerializedName("author_avatar")
    private String authorAvatar;

    @SerializedName("author_id")
    private String authorId;

    @SerializedName("author_name")
    private String authorName;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("message_text")
    private String messageText;

    public FlaskMessageResponse(String authorAvatar, String authorId, String authorName, String createdAt, Long messageId, String messageText) {
        this.authorAvatar = authorAvatar;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.messageId = messageId.toString();
        this.messageText = messageText;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId.toString();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}