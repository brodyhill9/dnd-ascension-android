package com.example.dndascension.interfaces

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T> String.fromJson() = Gson().fromJson<T>(this, object: TypeToken<T>() {}.type)
inline fun String.toJson() = Gson().toJson(this)

fun <T> T.serializeToMap(): HashMap<String, String> {
    return convert()
}
inline fun <I, reified O> I.convert(): O {
    val json = Gson().toJson(this)
    return Gson().fromJson(json, object : TypeToken<O>() {}.type)
}