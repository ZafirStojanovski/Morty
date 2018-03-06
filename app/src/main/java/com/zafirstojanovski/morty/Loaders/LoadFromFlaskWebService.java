package com.zafirstojanovski.morty.Loaders;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Zafir Stojanovski on 3/4/2018.
 */

public interface LoadFromFlaskWebService {
    @GET("/getMessages")
    Call<List<FlaskMessageResponse>> getMessages (@QueryMap Map<String, String> options);
}