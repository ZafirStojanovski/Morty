package com.zafirstojanovski.morty.FlaskDatabase;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.zafirstojanovski.morty.GetUserId.UserIdResponse;
import com.zafirstojanovski.morty.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.zafirstojanovski.morty.Fragments.ChatFragment.USER_ID;

public class DeleteFromFlaskIntentService extends IntentService {

    private FlaskWebService service;

    public DeleteFromFlaskIntentService() {
        super("DeleteFromFlaskIntentService");
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

        service = retrofit.create(FlaskWebService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Long userId = intent.getLongExtra(USER_ID, 0L);

        Call<UserIdResponse> call = service.deleteMessagesFromUser(userId);

        call.enqueue(new Callback<UserIdResponse>() {
            @Override
            public void onResponse(Call<UserIdResponse> call, Response<UserIdResponse> response) {
                try {
                    Log.i("FlaskDeleteSucc", response.body().userId + "");
                }catch (Exception e){
                    Log.e("FlaskDeleteExc", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserIdResponse> call, Throwable t) {
                Log.e("FlaskDeleteFail", t.getMessage());
            }
        });
    }
}