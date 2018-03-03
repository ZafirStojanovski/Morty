package com.zafirstojanovski.morty.RoomPersistance;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Zafir Stojanovski on 3/3/2018.
 */

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toTime(Date value) {
        return value == null ? null : value.getTime();
    }
}