package com.example.dndascension.interfaces

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T> String.fromJson() = Gson().fromJson<T>(this, object: TypeToken<T>() {}.type)
