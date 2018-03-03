package com.zafirstojanovski.morty.RoomPersistance;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.zafirstojanovski.morty.ChatkitEssentials.Message;
import static com.zafirstojanovski.morty.Fragments.ChatFragment.MESSAGE;
import static com.zafirstojanovski.morty.Fragments.ChatFragment.SHARED_PREFERENCES;

public class SaveMessageIntentService extends IntentService {

    public static final int STORED_MESSAGES_LIMIT = 20;
    public static final String STORED_MESSAGES = "com.zafirstojanovski.morty.RoomPersistance.SaveMessageIntentService.STORED_MESSAGES";

    SharedPreferences sharedPreferences;

    public SaveMessageIntentService() {
        super("SaveMessageIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Message message = (Message) intent.getSerializableExtra(MESSAGE);
        AppDatabase appDatabase = AppDatabase.getAppDatabase(getApplicationContext());

        int storedMessages = getStoredMessages();

        if (storedMessages >= STORED_MESSAGES_LIMIT) {
            String deleteMessageId = (Long.parseLong(message.getId()) - STORED_MESSAGES_LIMIT) + "";
            freeUpSpotInDatabase(appDatabase, deleteMessageId);
        } else {
            storedMessages++;
            updateStoredMessages(storedMessages);
        }

        appDatabase.messageHistoryDao().insertMessage(message);
    }

    private void freeUpSpotInDatabase(AppDatabase appDatabase, String deleteMessageId) {
        appDatabase.messageHistoryDao().deleteMessage(deleteMessageId);
    }

    private int getStoredMessages() {
        return sharedPreferences.getInt(STORED_MESSAGES, 0);
    }

    private void updateStoredMessages(int storedMessages){
        sharedPreferences
                .edit()
                .putInt(STORED_MESSAGES, storedMessages)
                .apply();
    }
}