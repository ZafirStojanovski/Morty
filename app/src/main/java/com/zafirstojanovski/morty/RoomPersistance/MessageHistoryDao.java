package com.zafirstojanovski.morty.RoomPersistance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zafirstojanovski.morty.ChatkitEssentials.Message;

import java.util.List;

/**
 * Created by Zafir Stojanovski on 3/3/2018.
 */

@Dao
public interface MessageHistoryDao {

    @Query("SELECT * FROM messageHistory")
    List<Message> getAll();

    @Insert
    long insertMessage(Message message);

    @Query("DELETE FROM messageHistory where id LIKE :id")
    int deleteMessage(String id);

    @Query("DELETE FROM messageHistory")
    void deleteAllMessages();
}