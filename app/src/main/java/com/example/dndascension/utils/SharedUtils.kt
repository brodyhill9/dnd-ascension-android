package com.example.dndascension.utils

fun spellLevelOrdinal(i: Int) : String {
    if (i == 0) return "Cantrip"
    return "$i" + if (i % 100 in 11..13) "th" else when (i % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}