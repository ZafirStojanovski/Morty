package com.zafirstojanovski.morty.GetUserId;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Zafir Stojanovski on 3/3/2018.
 */

public class UserIdResponse {
    @Expose
    @SerializedName("user_id")
    public long userId;
}
