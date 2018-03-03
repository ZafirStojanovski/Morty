package com.zafirstojanovski.morty.AskMorty;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

class MortyResponse {
    @Expose
    @SerializedName("response")
    public String response;
}
