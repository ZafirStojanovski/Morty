package com.zafirstojanovski.morty.FlaskDatabase;

import com.zafirstojanovski.morty.ChatkitEssentials.Message;
import java.io.Serializable;

/**
 * Created by Zafir Stojanovski on 3/4/2018.
 */

public class MessageWrapper implements Serializable{

    private Integer id;
    private Message message;
    private long userId;

    public MessageWrapper(Message message, long userId) {
        this.message = message;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public long getUserId() {
        return userId;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}