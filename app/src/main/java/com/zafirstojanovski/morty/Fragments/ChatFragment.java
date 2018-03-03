package com.zafirstojanovski.morty.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeIntent;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.zafirstojanovski.morty.AskMorty.MortyIntentService;
import com.zafirstojanovski.morty.ChatkitEssentials.Author;
import com.zafirstojanovski.morty.ChatkitEssentials.Message;
import com.zafirstojanovski.morty.GetUserId.UserIdIntentService;
import com.zafirstojanovski.morty.Loaders.MessageLoader;
import com.zafirstojanovski.morty.R;
import com.zafirstojanovski.morty.RoomPersistance.AppDatabase;
import com.zafirstojanovski.morty.RoomPersistance.SaveMessageIntentService;

import static com.zafirstojanovski.morty.AskMorty.MortyIntentService.RESPONSE_RECEIVED;
import static com.zafirstojanovski.morty.AskMorty.MortyIntentService.RESPONSE;
import static com.zafirstojanovski.morty.GetUserId.UserIdIntentService.RESPONSE_USER_ID_RECEIVED;
import static com.zafirstojanovski.morty.GetUserId.UserIdIntentService.RESPONSE_USER_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

public class ChatFragment extends Fragment implements MessageInput.InputListener,
        LoaderManager.LoaderCallbacks<List<Message>>{

    private Activity activity;
    private Context context;
    private com.ibm.watson.developer_cloud.conversation.v1.model.Context chatContext = null;
    private long userId;

    private Long messageId;
    private MessagesList messagesList;
    private MessagesListAdapter<Message> adapter;
    private MessageInput inputView;
    private Author sender;
    private Author receiver;

    private AppDatabase appDatabase;
    private SharedPreferences sharedPreferences;

    public static final String RICK_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.RICK";
    public static final String MORTY_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.MORTY";
    public static final String STATEMENT = "com.zafirstojanovski.morty.Fragments.ChatFragment.STATEMENT";
    public static final String MESSAGE = "com.zafirstojanovski.morty.Fragments.ChatFragment.MESSAGE";
    public static final String MESSAGE_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.MESSAGE_ID";
    public static final String USER_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.USER_ID";
    public static final String SHARED_PREFERENCES = "com.zafirstojanovski.morty.Fragments.ChatFragment.SHARED_PREFERENCES";
    private static final double CONFIDENCE_THRESHOLD = 0.72;


    private UpdateChatReceiver updateChatReceiver;
    private UpdateUserIdReceiver updateUserIdReceiver;

    public ChatFragment() {}

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupContext();
        setupBroadcastReceivers();
        setupAppDatabase();
        setupChatKit(getView());
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(RESPONSE_RECEIVED);
        activity.registerReceiver(updateChatReceiver, filter);

        filter = new IntentFilter(RESPONSE_USER_ID_RECEIVED);
        activity.registerReceiver(updateUserIdReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(updateChatReceiver);
        activity.unregisterReceiver(updateUserIdReceiver);
        storeMessageId();
    }

    private void setupContext() {
        this.activity = this.getActivity();
        this.context = this.activity.getApplicationContext();

       sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        userId = sharedPreferences.getLong(USER_ID, 0L);

        // User hasn't been assigned an ID
        if (userId == 0L){
            getNewUserId();
        }
    }

    private void getNewUserId() {
        activity.startService(
                new Intent(
                        context,
                        UserIdIntentService.class
                )
        );
    }

    private void setupBroadcastReceivers() {
        this.updateChatReceiver = new UpdateChatReceiver();
        this.updateUserIdReceiver = new UpdateUserIdReceiver();
    }

    private void setupAppDatabase() {
        this.appDatabase = AppDatabase.getAppDatabase(context);
        getLoaderManager()
                .initLoader(0, null, this)
                .forceLoad();
    }

    private void setupChatKit(View rootView) {
        messagesList = rootView.findViewById(R.id.messagesList);
        messageId = getMessageId();
        sender = new Author(RICK_ID, getString(R.string.rick_name), null);
        receiver = new Author(MORTY_ID, getString(R.string.morty_name), null);
        adapter = new MessagesListAdapter<>(sender.getId(), null);
        messagesList.setAdapter(adapter);
        inputView = rootView.findViewById(R.id.input);
        inputView.setInputListener(this);
    }

    private Long getMessageId(){
        return sharedPreferences.getLong(MESSAGE_ID, 0L);
    }

    private void storeMessageId(){
        sharedPreferences
                .edit()
                .putLong(MESSAGE_ID, messageId)
                .apply();
    }

    @Override
    public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
        return new MessageLoader(context, appDatabase);
    }

    @Override
    public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
        adapter.addToEnd(data, true);
    }

    @Override
    public void onLoaderReset(Loader<List<Message>> loader) {}

    @Override
    public boolean onSubmit(CharSequence input) {
        writeStatement(input.toString().trim());
        getWatsonResponse(input.toString().trim());
        return true;
    }

    private void getWatsonResponse(final String inputMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
                    service.setUsernameAndPassword(getString(R.string.username), getString(R.string.password));

                    InputData input = new InputData.Builder(inputMessage).build();
                    MessageOptions options = new MessageOptions.Builder(getString(R.string.workspace_id)).input(input).context(chatContext).build();
                    MessageResponse response = service.message(options).execute();

                    if (response != null) {
                        //Passing Context of last conversation
                        if (response.getContext() != null) {
                            //context.clear();
                            chatContext = response.getContext();
                        }
                        if (response.getOutput() != null && response.getOutput().containsKey("text")) {
                            Log.i("http response", response.toString());
                            ArrayList responseList = (ArrayList) response.getOutput().get("text");
                            if (responseList != null && responseList.size() > 0){
                                double confidence = 0;
                                final String watsonResponse = (String) responseList.get(0);

                                ArrayList<RuntimeIntent> intentsList = (ArrayList<RuntimeIntent>) response.getIntents();

                                // Check if there is any intent detected and the confidence level of it.
                                if (intentsList.size() > 0 && intentsList.get(0).containsKey("confidence")){
                                    confidence = (double) response.getIntents().get(0).get("confidence");
                                }

                                final double finalConfidence = confidence;

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        resolveResponse(watsonResponse, finalConfidence, inputMessage);
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void resolveResponse(String watsonResponse, double confidence, String originalInputMessage){
        if (confidence >= CONFIDENCE_THRESHOLD && !watsonResponse.contains(getString(R.string.negative_reponse))){
            writeResponse(watsonResponse);
        }
        else {
            getActivity().startService(
                    new Intent(
                            getActivity(),
                            MortyIntentService.class
                    ).putExtra(STATEMENT, originalInputMessage)
            );
        }
    }

    private class UpdateChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(RESPONSE);
            writeResponse(response);
        }
    }

    private class UpdateUserIdReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long userId = intent.getLongExtra(RESPONSE_USER_ID, 0L);
            storeUserId(userId);
        }
    }

    private void storeUserId(long userId) {
        this.userId = userId;
        sharedPreferences
                .edit()
                .putLong(USER_ID, userId)
                .apply();
    }

    private void writeStatement(String statement){
        Message messageFromSender = new Message((++messageId).toString(), statement , sender, new Date());
        adapter.addToStart(messageFromSender, true);
        saveToAppDatabase(messageFromSender);
    }

    private void writeResponse(final String response){
        Message messageFromReceiver = new Message((++messageId).toString(), response, receiver, new Date());
        adapter.addToStart(messageFromReceiver, true);
        saveToAppDatabase(messageFromReceiver);
    }

    private void saveToAppDatabase(final Message message){
        context.startService(
                new Intent(
                        context,
                        SaveMessageIntentService.class)
                .putExtra(MESSAGE, message)
        );
    }
}