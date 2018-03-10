package com.zafirstojanovski.morty.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.zafirstojanovski.morty.ChatkitEssentials.Author;
import com.zafirstojanovski.morty.ChatkitEssentials.Message;
import com.zafirstojanovski.morty.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Zafir Stojanovski on 3/5/2018.
 */

public class FlaskMessagesLoader extends AsyncTaskLoader<List<Message>> {

    private LoadFromFlaskWebService service;
    private String userId;
    private String lastMessageId;
    private List<Message> messages;

    public FlaskMessagesLoader(Context context, String userId, String lastMessageId){
        super(context);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.localhost_ip))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        service = retrofit.create(LoadFromFlaskWebService.class);
        this.userId = userId;
        this.lastMessageId = lastMessageId;
    }


    @Override
    public List<Message> loadInBackground() {
        this.messages = new ArrayList<>();
        Map<String, String> options = new HashMap<>();
        options.put("user_id", userId);
        options.put("last_message_id", lastMessageId);
        Call<List<FlaskMessageResponse>> call = service.getMessages(options);

        try {
            List<FlaskMessageResponse> flaskMessages = call.execute().body();

            if (flaskMessages != null){
                for (FlaskMessageResponse flaskMessage : flaskMessages) {
                    Author author = flaskMessage.getAuthorAvatar().equals("") ?
                            new Author(flaskMessage.getAuthorId(), flaskMessage.getAuthorName(), null) :
                            new Author(flaskMessage.getAuthorId(), flaskMessage.getAuthorName(), flaskMessage.getAuthorAvatar());

                    Date date = getDate(flaskMessage.getCreatedAt());

                    Message message = new Message(flaskMessage.getMessageId(), flaskMessage.getMessageText(), author, date);
                    messages.add(message);
                }
            }
        } catch (IOException e) {
            Log.e("FlaskMessagesLoader", e.getMessage());
        }

        return messages;
    }

    private Date getDate(String createdAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");
        try {
            return sdf.parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}