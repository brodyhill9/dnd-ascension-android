package com.example.dndascension.models

import com.example.dndascension.utils.spellLevelOrdinal

data class Spell (
    val spell_id: Int = 0,
    val spell_name: String = "",
    val spell_desc: String = "",
    val spell_level: Int = 0
) : Asset {
    override fun displayName(): String {
        return spell_name
    }
    override fun description(): String {
        return spellLevelOrdinal(spell_level)
    }
}