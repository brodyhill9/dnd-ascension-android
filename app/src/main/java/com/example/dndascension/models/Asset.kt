package com.example.dndascension.models

interface Asset {
    fun displayName(): String
    fun description(): String { return "" }
}