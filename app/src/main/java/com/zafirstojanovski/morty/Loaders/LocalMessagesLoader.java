package com.zafirstojanovski.morty.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.zafirstojanovski.morty.ChatkitEssentials.Message;
import com.zafirstojanovski.morty.Fragments.ChatFragment;
import com.zafirstojanovski.morty.RoomPersistance.AppDatabase;

import java.util.List;

/**
 * Created by Zafir Stojanovski on 3/3/2018.
 */

public class LocalMessagesLoader extends AsyncTaskLoader<List<Message>> {

    private AppDatabase appDatabase;
    private List<Message> messages;

    public LocalMessagesLoader(Context context, AppDatabase appDatabase) {
        super(context);
        this.appDatabase = appDatabase;
    }

    @Override
    protected void onStartLoading() {
        if (messages != null){
            deliverResult(messages);
        }
        if (messages == null || takeContentChanged()){
            forceLoad();
        }
    }

    @Override
    public List<Message> loadInBackground() {
        messages = appDatabase.messageHistoryDao().getAll();
        return messages;
    }
}