package com.example.dndascension.interfaces

import java.io.Serializable

interface SetValue : Asset, Serializable {
    var value_id: Int?
    var set_value: String
    var value_desc: String
    var set_name: String

    override fun id(): Int? {
        return value_id
    }
    override fun name(): String {
        return set_value
    }
    override fun desc(): String {
        return value_desc
    }
}