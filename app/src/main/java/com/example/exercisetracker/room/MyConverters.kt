package com.example.exercisetracker.room

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class MyConverters {

    private val gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<LatLng?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<LatLng?>?>() {}.type
        return gson.fromJson<List<LatLng?>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(latLng: List<LatLng?>?): String? {
        return gson.toJson(latLng)
    }

}