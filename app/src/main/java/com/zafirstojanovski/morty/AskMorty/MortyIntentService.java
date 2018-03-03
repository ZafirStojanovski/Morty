package com.zafirstojanovski.morty.AskMorty;

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

public class MortyIntentService extends IntentService {

    public static final String RESPONSE_RECEIVED = "com.zafirstojanovski.morty.AskMorty.MortyIntentService.RESPONSE_RECEIVED";
    public static final String RESPONSE = "com.zafirstojanovski.morty.AskMorty.MortyIntentService.RESPONSE";
    private static final String CLASS_NAME = "MortyIntentService";

    private MortyWebService service;

    public MortyIntentService() {
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

        service = retrofit.create(MortyWebService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String statement = intent.getStringExtra(STATEMENT);

        Call<MortyResponse> call = service.getAnswer(statement);

        call.enqueue(new Callback<MortyResponse>() {
            @Override
            public void onResponse(Call<MortyResponse> call, Response<MortyResponse> response) {
                MortyResponse mortyResponse = response.body();
                sendBroadcast(new Intent(RESPONSE_RECEIVED)
                        .putExtra(RESPONSE, mortyResponse.response));
            }

            @Override
            public void onFailure(Call<MortyResponse> call, Throwable t) {
                Log.i(CLASS_NAME, t.getMessage());
            }
        });
    }
}