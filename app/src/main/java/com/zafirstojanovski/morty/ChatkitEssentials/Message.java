package com.zafirstojanovski.morty.ChatkitEssentials;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Zafir Stojanovski on 2/19/2018.
 */

@Entity(tableName = "messageHistory")
public class Message implements IMessage, Serializable {

    @PrimaryKey
    private String id;

    @ColumnInfo(name="text")
    private String text;

    @Embedded
    private Author user;

    @ColumnInfo(name="created_at")
    private Date createdAt;

    public Message(String id, String text, Author user, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(Author user) {
        this.user = user;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}