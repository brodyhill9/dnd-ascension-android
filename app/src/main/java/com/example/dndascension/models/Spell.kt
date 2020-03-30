package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset
import com.example.dndascension.interfaces.JSONConvertable
import com.example.dndascension.utils.spellLevelOrdinal
import com.google.gson.annotations.SerializedName

data class Spell (
    @SerializedName("spell_id")
    val id: Int = 0,
    @SerializedName("spell_name")
    val name: String = "",
    @SerializedName("spell_desc")
    val desc: String = "",
    @SerializedName("spell_level")
    val level: Int = 0
) : Asset, JSONConvertable {
    override fun displayName(): String {
        return name
    }
    override fun description(): String {
        return spellLevelOrdinal(level)
    }
}