package com.zafirstojanovski.morty.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.zafirstojanovski.morty.ChatkitEssentials.Message;
import com.zafirstojanovski.morty.RoomPersistance.AppDatabase;

import java.util.List;

/**
 * Created by Zafir Stojanovski on 3/3/2018.
 */

public class MessageLoader extends AsyncTaskLoader<List<Message>> {

    private AppDatabase appDatabase;

    public MessageLoader(Context context, AppDatabase appDatabase) {
        super(context);
        this.appDatabase = appDatabase;
    }

    @Override
    public List<Message> loadInBackground() {
        return appDatabase.messageHistoryDao().getAll();
    }
}