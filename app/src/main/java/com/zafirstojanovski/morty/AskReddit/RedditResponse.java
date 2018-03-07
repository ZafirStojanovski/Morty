package com.zafirstojanovski.morty.AskReddit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

class RedditResponse {
    @Expose
    @SerializedName("response")
    public String response;
}
