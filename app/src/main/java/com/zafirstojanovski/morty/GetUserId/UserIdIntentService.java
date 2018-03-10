package com.zafirstojanovski.morty.GetUserId;

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

public class UserIdIntentService extends IntentService {

    private UserIdWebService service;

    public static final String RESPONSE_USER_ID = "com.zafirstojanovski.morty.GetUserId.UserIdIntentService.RESPONSE_USER_ID";
    public static final String RESPONSE_USER_ID_RECEIVED = "com.zafirstojanovski.morty.GetUserId.UserIdIntentService.RESPONSE_USER_ID_RECEIVED";
    private static final String CLASS_NAME = "UserIdIntentService";

    public UserIdIntentService() {
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

        service = retrofit.create(UserIdWebService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Call<UserIdResponse> call = service.getUserId();

        call.enqueue(new Callback<UserIdResponse>() {
            @Override
            public void onResponse(Call<UserIdResponse> call, Response<UserIdResponse> response) {
                try {
                    UserIdResponse userIdResponse = response.body();
                    sendBroadcast(new Intent(RESPONSE_USER_ID_RECEIVED)
                            .putExtra(RESPONSE_USER_ID, userIdResponse.userId));
                }catch (Exception e){
                    Log.e("UserIdResponseFail", e.getMessage());
                    sendBroadcast(new Intent(RESPONSE_USER_ID_RECEIVED)
                            .putExtra(RESPONSE_USER_ID, 0L));
                }
            }

            @Override
            public void onFailure(Call<UserIdResponse> call, Throwable t) {
                Log.i("UserIdResponseFail", t.getMessage());
            }
        });
    }
}