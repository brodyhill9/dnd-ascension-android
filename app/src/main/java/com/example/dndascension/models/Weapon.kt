package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset
import com.example.dndascension.utils.WeaponType
import com.example.dndascension.utils.weaponTypeList

data class Weapon (
    var weapon_id: Int? = null,
    var weapon_name: String = "",
    var weapon_desc: String = "",
    var weapon_type: WeaponType = WeaponType.values()[0],
    var cost: String = "",
    var damage: String = "",
    var weight: Int? = null,
    var weapon_props: String = ""

) : Asset {
    override fun id(): Int? {
        return weapon_id
    }
    override fun name(): String {
        return weapon_name
    }
    override fun tag(): String {
        return weaponType()
    }
    override fun desc(): String {
        return damage
    }
    override fun secSort(): String {
        return weapon_type.ordinal.toString()
    }

    fun weaponType(): String {
        return weaponTypeList()[weapon_type.ordinal]
    }
}