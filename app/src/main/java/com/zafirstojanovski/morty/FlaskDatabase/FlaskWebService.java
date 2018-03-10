package com.zafirstojanovski.morty.FlaskDatabase;

import com.zafirstojanovski.morty.GetUserId.UserIdResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

public interface FlaskWebService {
    @POST("/addMessage")
    Call<MessageWrapper> addMessage (@Body MessageWrapper messageWrapper);

    @DELETE("/deleteMessagesFromUser/{userId}")
    Call<UserIdResponse> deleteMessagesFromUser (@Path("userId") Long userId);
}