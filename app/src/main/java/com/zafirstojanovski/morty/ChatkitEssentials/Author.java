package com.zafirstojanovski.morty.ChatkitEssentials;

import android.arch.persistence.room.ColumnInfo;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;

/**
 * Created by Zafir Stojanovski on 2/19/2018.
 */

public class Author implements IUser, Serializable{

    @ColumnInfo(name="author_id")
    private String id;

    @ColumnInfo(name="author_name")
    private String name;

    @ColumnInfo(name="author_avatar")
    private String avatar;

    public Author(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
}