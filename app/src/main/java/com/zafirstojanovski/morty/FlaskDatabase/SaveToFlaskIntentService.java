package com.zafirstojanovski.morty.FlaskDatabase;

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
import static com.zafirstojanovski.morty.Fragments.ChatFragment.MESSAGE_WRAPPER;

public class SaveToFlaskIntentService extends IntentService {

    private SaveToFlaskWebService service;

    public SaveToFlaskIntentService() {
        super("SaveToFlaskIntentService");
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

        service = retrofit.create(SaveToFlaskWebService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MessageWrapper messageWrapper = (MessageWrapper) intent.getSerializableExtra(MESSAGE_WRAPPER);

        Call<MessageWrapper> call = service.addMessage(messageWrapper);

        call.enqueue(new Callback<MessageWrapper>() {
            @Override
            public void onResponse(Call<MessageWrapper> call, Response<MessageWrapper> response) {
                Log.i("SaveToFlaskSucc", response.body().getMessage().getId() + "");
            }

            @Override
            public void onFailure(Call<MessageWrapper> call, Throwable t) {
                Log.i("SaveToFlaskFail", t.getMessage());
            }
        });
    }
}