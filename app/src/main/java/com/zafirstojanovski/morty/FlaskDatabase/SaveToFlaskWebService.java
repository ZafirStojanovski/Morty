package com.zafirstojanovski.morty.FlaskDatabase;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

public interface SaveToFlaskWebService {
    @POST("/addMessage")
    Call<MessageWrapper> addMessage (@Body MessageWrapper messageWrapper);
}