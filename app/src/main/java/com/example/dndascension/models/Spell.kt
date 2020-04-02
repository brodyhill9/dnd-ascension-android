package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset
import com.example.dndascension.interfaces.JSONConvertable
import com.example.dndascension.utils.spellLevelSchool

data class Spell (
    var spell_id: Int = 0,
    var spell_name: String = "",
    var spell_desc: String = "",
    var spell_level: Int = 0,
    var higher_level: String = "",
    var spell_range: String = "",
    var components: String = "",
    var duration: String = "",
    var ritual: Boolean = false,
    var casting_time: String = "",
    var spell_school: String = ""

) : Asset, JSONConvertable {
    override fun displayName(): String {
        return spell_name
    }
    override fun description(): String {
        return spellLevelSchool(spell_level, spell_school)
    }
}