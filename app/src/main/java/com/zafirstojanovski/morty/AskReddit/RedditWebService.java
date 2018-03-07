package com.zafirstojanovski.morty.AskReddit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

public interface RedditWebService {
    @GET("/getAnswer")
    Call<RedditResponse> getAnswer(@Query("statement") String statement);
}