package com.zafirstojanovski.morty.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeIntent;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.zafirstojanovski.morty.AskReddit.RedditIntentService;
import com.zafirstojanovski.morty.ChatkitEssentials.Author;
import com.zafirstojanovski.morty.ChatkitEssentials.Message;
import com.zafirstojanovski.morty.FlaskDatabase.DeleteFromFlaskIntentService;
import com.zafirstojanovski.morty.FlaskDatabase.SaveToFlaskIntentService;
import com.zafirstojanovski.morty.FlaskDatabase.MessageWrapper;
import com.zafirstojanovski.morty.GetUserId.UserIdIntentService;
import com.zafirstojanovski.morty.Loaders.FlaskMessagesLoader;
import com.zafirstojanovski.morty.Loaders.LocalMessagesLoader;
import com.zafirstojanovski.morty.R;
import com.zafirstojanovski.morty.RoomPersistance.AppDatabase;
import com.zafirstojanovski.morty.RoomPersistance.DeleteAllMessagesIntentService;
import com.zafirstojanovski.morty.RoomPersistance.SaveMessageIntentService;

import static com.zafirstojanovski.morty.AskReddit.RedditIntentService.RESPONSE_RECEIVED;
import static com.zafirstojanovski.morty.AskReddit.RedditIntentService.RESPONSE;
import static com.zafirstojanovski.morty.GetUserId.UserIdIntentService.RESPONSE_USER_ID_RECEIVED;
import static com.zafirstojanovski.morty.GetUserId.UserIdIntentService.RESPONSE_USER_ID;
import static com.zafirstojanovski.morty.Fragments.SettingsFragment.CENSOR_SWITCH;
import static com.zafirstojanovski.morty.Fragments.SettingsFragment.SYNC_SWITCH;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */


public class ChatFragment extends Fragment implements MessageInput.InputListener {

    private Activity activity;
    private Context context;
    private com.ibm.watson.developer_cloud.conversation.v1.model.Context chatContext = null;
    private Long userId;

    private Long messageId;
    private String lastMessageId;
    private MessagesList messagesList;
    private MessagesListAdapter<Message> adapter;
    private MessageInput inputView;
    private Author rick;
    private Author morty;
    private ImageLoader imageLoader;

    private String evilMortyImage = new StringBuilder().append(R.drawable.realistic_evil_morty).toString();

    private Handler mortyTypingHandler;
    private Runnable mortyTypingRunnable;
    private Message mortyTypingMessage;
    private static final String MORTY_TYPING_ID = "-1";
    private static final String[] mortyTypingTexts = {"Morty is thinking", "Morty is thinking.", "Morty is thinking..", "Morty is thinking..."};
    private int mortyTypingCounter;

    private MessagesListAdapter.OnLoadMoreListener loadMoreListener;

    private LoaderManager.LoaderCallbacks<List<Message>> localDataLoaderListener;
    private LoaderManager.LoaderCallbacks<List<Message>> flaskDataLoaderListener;
    private static final int LOCAL_DATA_LOADER_ID = 1;
    private static final int FLASK_DATA_LOADER_ID = 2;

    private AppDatabase appDatabase;
    private SharedPreferences sharedPreferences;

    public static final String RICK_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.RICK";
    public static final String MORTY_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.MORTY";
    public static final String STATEMENT = "com.zafirstojanovski.morty.Fragments.ChatFragment.STATEMENT";
    public static final String MESSAGE = "com.zafirstojanovski.morty.Fragments.ChatFragment.MESSAGE";
    public static final String MESSAGE_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.MESSAGE_ID";
    public static final String USER_ID = "com.zafirstojanovski.morty.Fragments.ChatFragment.USER_ID";
    public static final String SHARED_PREFERENCES = "com.zafirstojanovski.morty.Fragments.ChatFragment.SHARED_PREFERENCES";
    public static final String MESSAGE_WRAPPER = "com.zafirstojanovski.morty.Fragments.ChatFragment.MESSAGE_WRAPPER";
    private static final double CONFIDENCE_THRESHOLD = 0.76;

    private UpdateChatReceiver updateChatReceiver;
    private UpdateUserIdReceiver updateUserIdReceiver;
    private NetworkChangeReceiver networkChangeReceiver;

    public ChatFragment() {}


    public static ChatFragment newInstance() {
        return new ChatFragment();
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
        setupChatKit(getView());
        setupBroadcastReceivers();
        setupAppDatabase();
        setupLoaders();
        loadFromLocalDatabase();
        setupLoadMoreListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter updateChatFilter = new IntentFilter(RESPONSE_RECEIVED);
        activity.registerReceiver(updateChatReceiver, updateChatFilter);

        IntentFilter updateUserIdFilter = new IntentFilter(RESPONSE_USER_ID_RECEIVED);
        activity.registerReceiver(updateUserIdReceiver, updateUserIdFilter);

        IntentFilter networkChangeFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        activity.registerReceiver(networkChangeReceiver, networkChangeFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(updateChatReceiver);
        activity.unregisterReceiver(updateUserIdReceiver);
        activity.unregisterReceiver(networkChangeReceiver);
    }


    private void setupContext() {
        activity = this.getActivity();
        context = activity.getApplicationContext();

        sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong(USER_ID, 0L);
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
        this.networkChangeReceiver = new NetworkChangeReceiver();
    }


    private void setupLoaders() {
        localDataLoaderListener = new LoaderManager.LoaderCallbacks<List<Message>>() {
            @Override
            public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
                return new LocalMessagesLoader(context, appDatabase);
            }

            @Override
            public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
                if (data.size() > 0){
                    adapter.addToEnd(data, true); // fill data to adapter
                    lastMessageId = data.get(data.size() - 1).getId();
                }
                else {
                    lastMessageId = "0";
                }
                adapter.setLoadMoreListener(loadMoreListener); // start listening for onLoadMore events now.
            }

            @Override
            public void onLoaderReset(Loader<List<Message>> loader) { }
        };

        flaskDataLoaderListener = new LoaderManager.LoaderCallbacks<List<Message>>() {
            @Override
            public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
                return new FlaskMessagesLoader(context, userId.toString(), lastMessageId);
            }

            @Override
            public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
                final List<Message> messages = data;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (messages.size() > 0){
                            lastMessageId = messages.get(0).getId();
                            adapter.addToEnd(messages, true);
                        }
                    }
                }, 1000);

            }

            @Override
            public void onLoaderReset(Loader<List<Message>> loader) { }
        };
    }

    private void setupAppDatabase() {
        this.appDatabase = AppDatabase.getAppDatabase(context);
    }

    private void loadFromLocalDatabase() {
        getLoaderManager()
                .initLoader(LOCAL_DATA_LOADER_ID, null, localDataLoaderListener);
    }

    private void loadFromFlaskDatabase() {
        getLoaderManager()
                .restartLoader(FLASK_DATA_LOADER_ID, null, flaskDataLoaderListener)
                .forceLoad();
    }

    private void setupChatKit(View rootView) {
        setupImageLoader();
        messagesList = rootView.findViewById(R.id.messagesList);
        messageId = getMessageId();
        rick = new Author(RICK_ID, getString(R.string.rick_name), null);
        morty = new Author(MORTY_ID, getString(R.string.morty_name), evilMortyImage);
        adapter = new MessagesListAdapter<>(rick.getId(), imageLoader);
        messagesList.setAdapter(adapter);
        inputView = rootView.findViewById(R.id.input);
        inputView.setInputListener(this);
        setupTypingHandler();
    }

    private void setupImageLoader() {
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                imageView.setBackgroundResource(Integer.parseInt(url));
            }
        };
    }

    private void setupTypingHandler() {

        mortyTypingHandler = new Handler();
        mortyTypingRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    updateMortyTypingMessage();
                } finally {
                    mortyTypingHandler.postDelayed(mortyTypingRunnable, 1000);
                }
            }
        };
    }

    private void updateMortyTypingMessage() {
        mortyTypingCounter = mortyTypingCounter % 4;
        mortyTypingMessage.setText(mortyTypingTexts[mortyTypingCounter]);
        mortyTypingMessage.setCreatedAt(new Date());
        adapter.notifyDataSetChanged();
        mortyTypingCounter++;
    }

    private void startUpdatingMortyTyping(){
        mortyTypingMessage = new Message(MORTY_TYPING_ID,"", morty, new Date());
        mortyTypingCounter = 0;
        adapter.addToStart(mortyTypingMessage, true);
        mortyTypingRunnable.run();
    }

    private void stopUpdatingMortyTyping(){
        mortyTypingHandler.removeCallbacks(mortyTypingRunnable);
        adapter.deleteById(MORTY_TYPING_ID);
        adapter.notifyDataSetChanged();
    }


    private void setupLoadMoreListener() {
        loadMoreListener = new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (Long.parseLong(lastMessageId) > 1 ) {
                    loadFromFlaskDatabase();
                }
            }
        };
    }

    private Long getMessageId(){
        return sharedPreferences
                .getLong(MESSAGE_ID, 0L);
    }

    private void storeMessageId(){
        sharedPreferences
                .edit()
                .putLong(MESSAGE_ID, messageId)
                .apply();
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        if (checkInternetConnection()){
            if (mortyResponded()){
                writeStatement(input.toString().trim());
                return true;
            } else {
                Toast.makeText(activity, R.string.wait_morty, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(activity, R.string.connect_to_internet, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean mortyResponded() {
        return messageId % 2 == 0;
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
                            Log.i("HTTP_RESPONSE", response.toString());
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

    /**
     * Resolve whether Watson's or Reddit's response is going to be taken.
     */
    private void resolveResponse(String watsonResponse, double confidence, final String originalInputMessage){
        if (validateResponse(watsonResponse, confidence)){
            writeResponse(watsonResponse);
        }
        else if (isSwitchChecked(CENSOR_SWITCH)){
            writeResponse(getString(R.string.diplomatic_response));
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startUpdatingMortyTyping();
                    getActivity().startService(
                            new Intent(
                                    getActivity(),
                                    RedditIntentService.class
                            ).putExtra(STATEMENT, originalInputMessage)
                    );
                }
            }, 375);
        }
    }

    /**
        Validate whether or not to write down Watson's response
     */
    private boolean validateResponse(String watsonResponse, double confidence) {
        return confidence >= CONFIDENCE_THRESHOLD
                && !watsonResponse.contains(getString(R.string.negative_reponse))
                && !watsonResponse.trim().isEmpty();
    }

    /**
     * A response from Reddit has been received
     */
    private class UpdateChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopUpdatingMortyTyping();
            String response = intent.getStringExtra(RESPONSE);
            writeResponse(response);
        }
    }

    /**
     * A new user id has been received
     */
    private class UpdateUserIdReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Long newUserId = intent.getLongExtra(RESPONSE_USER_ID, 0L);
            storeUserId(newUserId);
        }
    }

    /**
        Internet connection state has changed
     */
    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(checkInternetConnection() && !hasUserId())
            {
                getNewUserId();
            }
        }
    }

    private void storeUserId(long userId) {
        this.userId = userId;
        sharedPreferences
                .edit()
                .putLong(USER_ID, userId)
                .apply();
    }

    public void writeStatement(String statement){
        Message messageFromSender = new Message((++messageId).toString(), statement, rick, new Date());
        adapter.addToStart(messageFromSender, true);
        storeMessageId();
        saveToAppDatabase(messageFromSender);
        saveToServerDatabase(messageFromSender);
        getWatsonResponse(statement);
    }

    private void writeResponse(final String response){
        Message messageFromReceiver = new Message((++messageId).toString(), response, morty, new Date());
        adapter.addToStart(messageFromReceiver, true);
        storeMessageId();
        saveToAppDatabase(messageFromReceiver);
        saveToServerDatabase(messageFromReceiver);
    }

    private void saveToAppDatabase(final Message message){
        if (isSwitchChecked(SYNC_SWITCH)){
            context.startService(
                    new Intent(
                            context,
                            SaveMessageIntentService.class
                    ).putExtra(MESSAGE, message)
            );
        }
    }

    private void saveToServerDatabase(final Message message){
        if (checkInternetConnection() && hasUserId() && isSwitchChecked(SYNC_SWITCH)){
            MessageWrapper messageWrapper = new MessageWrapper(message, userId);
            context.startService(
                    new Intent(
                            context,
                            SaveToFlaskIntentService.class
                    ).putExtra(MESSAGE_WRAPPER, messageWrapper)
            );
        }
        Log.i("SaveToServerDatabase", "No Internet / ID assigned / Switch turned off");
    }

    private boolean checkInternetConnection(){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private Boolean isSwitchChecked(String switchName){
        return sharedPreferences
                    .getBoolean(switchName, false);
    }

    private boolean hasUserId(){
        return userId != 0L;
    }

    public void deleteMessages(){
        deleteMessagesFromChat();
        deleteMessagesFromAppDatabase();
        deleteMessagesFromServerDatabase();
        resetMessageIdCounters();
    }


    private void deleteMessagesFromChat() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    private void deleteMessagesFromAppDatabase() {
        context.startService(
                new Intent(
                        activity,
                        DeleteAllMessagesIntentService.class
                )
        );
    }

    private void deleteMessagesFromServerDatabase() {
        if (checkInternetConnection()){
            context.startService(
                    new Intent(
                            activity,
                            DeleteFromFlaskIntentService.class
                    ).putExtra(USER_ID, userId)
            );
        }
    }

    private void resetMessageIdCounters() {
        messageId = 0L;
        lastMessageId = "0";
        storeMessageId();
    }
}