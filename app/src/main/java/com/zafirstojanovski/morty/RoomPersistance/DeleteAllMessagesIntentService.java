package com.zafirstojanovski.morty.RoomPersistance;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.util.Log;

import static com.zafirstojanovski.morty.Fragments.ChatFragment.SHARED_PREFERENCES;
import static com.zafirstojanovski.morty.RoomPersistance.SaveMessageIntentService.STORED_MESSAGES;

public class DeleteAllMessagesIntentService extends IntentService {

    SharedPreferences sharedPreferences;

    public DeleteAllMessagesIntentService() {
        super("DeleteAllMessagesIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppDatabase appDatabase = AppDatabase.getAppDatabase(getApplicationContext());

        try {
            appDatabase.messageHistoryDao().deleteAllMessages();
            updateStoredMessages(0);
        }catch (SQLException e){
            Log.e("DeleteMessagesException", e.getMessage());
        }
    }

    private void updateStoredMessages(int storedMessages){
        sharedPreferences
                .edit()
                .putInt(STORED_MESSAGES, storedMessages)
                .apply();
    }
}