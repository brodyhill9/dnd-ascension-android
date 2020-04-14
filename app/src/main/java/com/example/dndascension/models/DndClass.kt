package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset

data class DndClass (
    var class_id: Int? = null,
    var class_name: String = "",
    var hit_die: String = "",
    var str: Boolean = false,
    var dex: Boolean = false,
    var con: Boolean = false,
    var intel: Boolean = false,
    var wis: Boolean = false,
    var cha: Boolean = false,
    var subclasses: MutableList<DndClass> = mutableListOf(),
    var class_traits: MutableList<Trait> = mutableListOf(),
    var parent_id: Int? = null,
    var delete: Boolean = false,
    var index: Int? = null
) : Asset {
    override fun id(): Int? {
        return class_id
    }
    override fun name(): String {
        return class_name
    }
    override fun isNewAllowDelete(): Boolean {
        return isNew() && index != null
    }
    override fun toString(): String {
        if (delete) {
            return "$class_name (DELETED)"
        } else if (isNew()) {
            return "$class_name (NEW)"
        }
        return class_name;
    }

    fun isParent(): Boolean {
        return parent_id ?: 0 < 1
    }
}