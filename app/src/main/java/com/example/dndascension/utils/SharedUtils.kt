package com.example.dndascension.utils

fun armorTypeList(): List<String> {
    val armorTypes = mutableListOf<String>()
    for (type in ArmorType.values()) {
        armorTypes.add(when (type) {
            ArmorType.Shield -> type.toString()
            else ->  "$type Armor"
        })
    }
    return armorTypes
}

fun modToString(mod: Int): String {
    if (mod > 0) {
        return "+$mod"
    } else {
        return mod.toString()
    }
}

fun spellLevelOrdinal(i: Int): String {
    if (i == 0) return "Cantrip"
    return "$i" + if (i % 100 in 11..13) "th" else when (i % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    } + "-level"
}

fun spellLevelOrdinalList(): List<String> {
    val spellLevels = mutableListOf<String>()
    for (x in 0..9) {
        spellLevels.add(spellLevelOrdinal(x))
    }
    return spellLevels
}

fun spellLevelSchool(i: Int, school: SpellSchool) : String {
    val ordinal = spellLevelOrdinal(i)
    if (ordinal == "Cantrip") {
        return "$school $ordinal"
    } else {
        return "$ordinal ${school}"
    }
}

fun weaponTypeList(): List<String> {
    val weaponTypes = mutableListOf<String>()
    for (type in WeaponType.values()) {
        weaponTypes.add(when (type) {
            WeaponType.SimpleMelee -> "Simple Melee"
            WeaponType.SimpleRanged -> "Simple Ranged"
            WeaponType.MartialMelee -> "Martial Melee"
            WeaponType.MartialRanged -> "Martial Ranged"
        })
    }
    return weaponTypes
}