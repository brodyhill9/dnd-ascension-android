package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset
import com.example.dndascension.utils.modToString

data class Race (
    var race_id: Int? = null,
    var race_name: String = "",
    var str: Int = 0,
    var dex: Int = 0,
    var con: Int = 0,
    var intel: Int = 0,
    var wis: Int = 0,
    var cha: Int = 0,
    var speed: Int = 0,
    var subraces: MutableList<Race> = mutableListOf(),
    var race_traits: MutableList<Trait> = mutableListOf(),
    var parent_id: Int? = null,
    var delete: Boolean = false,
    var index: Int? = null
) : Asset {
    override fun id(): Int? {
        return race_id
    }
    override fun name(): String {
        return race_name
    }
    override fun isNewAllowDelete(): Boolean {
        return isNew() && index != null
    }
    override fun toString(): String {
        if (delete) {
            return "$race_name (DELETED)"
        } else if (isNew()) {
            return "$race_name (NEW)"
        }
        return race_name;
    }

    fun speed(): String {
        return "$speed feet"
    }
    fun str(): String {
        return modToString(str)
    }
    fun dex(): String {
        return modToString(dex)
    }
    fun con(): String {
        return modToString(con)
    }
    fun intel(): String {
        return modToString(intel)
    }
    fun wis(): String {
        return modToString(wis)
    }
    fun cha(): String {
        return modToString(cha)
    }
    fun isParent(): Boolean {
        return parent_id ?: 0 < 1
    }
    fun hasSubraces(): Boolean {
        return subraces.count() > 0
    }
}