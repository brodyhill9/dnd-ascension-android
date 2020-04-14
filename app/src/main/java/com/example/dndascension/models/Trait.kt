package com.example.dndascension.models

import com.example.dndascension.interfaces.Asset

data class Trait (
    var trait_id: Int? = null,
    var race_id: Int? = null,
    var trait_name: String = "",
    var trait_desc: String = "",
    var delete: Boolean = false,
    var index: Int? = null
) : Asset {
    override fun id(): Int? {
        return trait_id
    }
    override fun name(): String {
        return trait_name
    }
    override fun desc(): String {
        return trait_desc
    }
    override fun isNewAllowDelete(): Boolean {
        return isNew() && index != null
    }
    override fun toString(): String {
        if (delete) {
            return "$trait_name (DELETED)"
        } else if (isNew()) {
            return "$trait_name (NEW)"
        }
        return trait_name;
    }
}