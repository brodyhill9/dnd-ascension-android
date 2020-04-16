package com.example.dndascension.models

import com.example.dndascension.interfaces.JSONConvertable
import com.example.dndascension.utils.modToString
import java.io.Serializable

data class Character (
    var char_id: Int? = null,
    var char_name: String = "",
    var lvl: Int = 0,
    var str: Int = 10,
    var dex: Int = 10,
    var con: Int = 10,
    var intel: Int = 10,
    var wis: Int = 10,
    var cha: Int = 10,
    var armor_class: Int = 10,
    var caster: Boolean = false,

    var race_id: Int? = null,
    var race_name: String = "",
    var speed: Int = 0,
    var subrace_id: Int? = null,
    var subrace_name: String = "",
    var race_traits: MutableList<Trait> = mutableListOf(),

    var class_id: Int? = null,
    var class_name: String = "",
    var hit_die: String = "",
    var subclass_id: Int? = null,
    var subclass_name: String = "",
    var class_traits: MutableList<Trait> = mutableListOf(),

    var background_id: Int? = null,
    var background_name: String = "",

    var created_by: String = ""
) : Serializable, JSONConvertable {
    fun isNew(): Boolean { return char_id ?: 0 < 1 }

    fun raceClassLevel(): String { return "$subrace_name $class_name $lvl" }
    fun raceSubclassLevel(): String { return "$subrace_name $class_name ($subclass_name) $lvl" }

    fun strMod(): String { return mod(str) }
    fun dexMod(): String { return mod(dex) }
    fun conMod(): String { return mod(con) }
    fun intelMod(): String { return mod(intel) }
    fun wisMod(): String { return mod(wis) }
    fun chaMod(): String { return mod(cha) }

    fun strModScore(): String { return "${strMod()} ($str)" }
    fun dexModScore(): String { return "${dexMod()} ($dex)" }
    fun conModScore(): String { return "${conMod()} ($con)" }
    fun intelModScore(): String { return "${intelMod()} ($intel)" }
    fun wisModScore(): String { return "${wisMod()} ($wis)" }
    fun chaModScore(): String { return "${chaMod()} ($cha)" }

    fun profBonus(): String { return "${((lvl-1)/4)+2}" }

    private fun mod(score: Int): String {
        var baseScore = score-10
        baseScore = if (baseScore >= 0) baseScore else baseScore - 1
        return modToString(baseScore/2)
    }
}