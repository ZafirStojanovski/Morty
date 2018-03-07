package com.zafirstojanovski.morty.AskReddit;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.zafirstojanovski.morty.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.zafirstojanovski.morty.Fragments.ChatFragment.STATEMENT;

public class RedditIntentService extends IntentService {

    public static final String RESPONSE_RECEIVED = "com.zafirstojanovski.morty.AskReddit.RedditIntentService.RESPONSE_RECEIVED";
    public static final String RESPONSE = "com.zafirstojanovski.morty.AskReddit.RedditIntentService.RESPONSE";
    private static final String CLASS_NAME = "RedditIntentService";

    private RedditWebService service;

    public RedditIntentService() {
        super(CLASS_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.localhost_ip))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        service = retrofit.create(RedditWebService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String statement = intent.getStringExtra(STATEMENT);

        Call<RedditResponse> call = service.getAnswer(statement);

        call.enqueue(new Callback<RedditResponse>() {
            @Override
            public void onResponse(Call<RedditResponse> call, Response<RedditResponse> response) {
                RedditResponse redditResponse = response.body();
                sendBroadcast(new Intent(RESPONSE_RECEIVED)
                        .putExtra(RESPONSE, redditResponse.response));
            }

            @Override
            public void onFailure(Call<RedditResponse> call, Throwable t) {
                Log.i(CLASS_NAME, t.getMessage());
            }
        });
    }
}