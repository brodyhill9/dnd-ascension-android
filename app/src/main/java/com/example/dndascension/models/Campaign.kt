package com.example.dndascension.models

import com.example.dndascension.interfaces.JSONConvertable
import java.io.Serializable

data class Campaign (
    var camp_id: Int? = null,
    var camp_name: String = "",
    var chars: MutableList<Character> = mutableListOf(),
    var is_owner: Boolean = false,
    var created_by: String = ""
) : Serializable, JSONConvertable {
    fun isNew(): Boolean { return camp_id ?: 0 < 1 }
    override fun toString(): String {
        return camp_name
    }
}