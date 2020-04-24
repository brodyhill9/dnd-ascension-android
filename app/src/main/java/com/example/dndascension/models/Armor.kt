package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset
import com.example.dndascension.utils.ArmorType
import com.example.dndascension.utils.armorTypeList

data class Armor (
    var armor_id: Int? = null,
    var armor_name: String = "",
    var armor_desc: String = "",
    var armor_type: ArmorType = ArmorType.values()[0],
    var cost: String = "",
    var armor_class: String = "",
    var strength: Int? = null,
    var stealth_dis: Boolean = false,
    var weight: Int? = null

) : Asset {
    override fun id(): Int? {
        return armor_id
    }
    override fun name(): String {
        return armor_name
    }
    override fun tag(): String {
        return armorType()
    }
    override fun desc(): String {
        return armor_class
    }
    override fun secSort(): String {
        return armor_type.ordinal.toString()
    }

    fun armorType(): String {
        return armorTypeList()[armor_type.ordinal]
    }
}