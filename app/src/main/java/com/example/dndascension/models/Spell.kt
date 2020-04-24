package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset
import com.example.dndascension.utils.SpellSchool
import com.example.dndascension.utils.spellLevelOrdinal

data class Spell (
    var spell_id: Int? = null,
    var spell_name: String = "",
    var spell_desc: String = "",
    var spell_level: Int = 0,
    var higher_level: String = "",
    var spell_range: String = "",
    var components: String = "",
    var duration: String = "",
    var ritual: Boolean = false,
    var casting_time: String = "",
    var spell_school: SpellSchool = SpellSchool.values()[0]

) : Asset {
    override fun id(): Int? {
        return spell_id
    }
    override fun name(): String {
        return spell_name
    }
    override fun tag(): String {
        return spellLevelOrdinal(spell_level)
    }
    override fun desc(): String {
        return spell_desc
    }

    override fun secSort(): String {
        return spell_level.toString()
    }
}