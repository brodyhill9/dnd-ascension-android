package com.example.dndascension.interfaces

import java.io.Serializable

interface Asset : Serializable, JSONConvertable {
    fun id(): Int?
    fun name(): String
    fun tag(): String { return "" }
    fun desc(): String { return ""}
    fun isNew(): Boolean { return id() ?: 0 < 1 }
}