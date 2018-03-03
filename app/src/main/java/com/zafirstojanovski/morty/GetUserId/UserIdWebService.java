package com.zafirstojanovski.morty.GetUserId;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Zafir Stojanovski on 3/3/2018.
 */

public interface UserIdWebService {
    @GET("/getUserId")
    Call<UserIdResponse> getUserId();
}