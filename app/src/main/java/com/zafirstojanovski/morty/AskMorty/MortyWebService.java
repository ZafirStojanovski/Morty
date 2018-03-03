package com.zafirstojanovski.morty.AskMorty;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

public interface MortyWebService {
    @GET("/getAnswer")
    Call<MortyResponse> getAnswer(@Query("statement") String statement);
}