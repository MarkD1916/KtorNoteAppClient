package com.vmakd1916gmail.com.login_logout_register.DB

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class TypeConverter {
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromList(list: List<String>): String{
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(string: String): List<String>{
        return Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)
    }
}