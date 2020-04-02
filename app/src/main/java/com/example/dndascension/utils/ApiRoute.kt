package com.example.dndascension.utils

import com.amazonaws.mobile.client.AWSMobileClient
import com.android.volley.Request
import com.example.dndascension.models.Spell

sealed class ApiRoute {
    data class GetSpells(val placeholder: String = "") : ApiRoute()
    data class GetSpell(var id: Int) : ApiRoute()
    data class SaveSpell(var spell: Spell) : ApiRoute()

    val timeOut: Int
        get() {
            return 3000
        }

    val url: String
        get() {
            return when (this) {
                is GetSpells, is SaveSpell -> "https://ctctjf95w6.execute-api.us-east-2.amazonaws.com/dev"
                is GetSpell -> "https://ctctjf95w6.execute-api.us-east-2.amazonaws.com/dev?spell_id=${this.id}"
            }
        }
    val httpMethod: Int
        get() {
            return when (this) {
                else -> Request.Method.GET
            }
        }

    val params: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            when (this) {
                is SaveSpell -> {
                    map["spell_id"] = this.spell.spell_id.toString()
                }
            }
            return map
        }

    val headers: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            map["Accept"] = "application/json"
            map["Content-Type"] = "application/json;charset=utf-8"
            map["DndUser"] = AWSMobileClient.getInstance().userAttributes.get("preferred_username").toString()
            //map["Authorization"] = "Bearer ${AWSMobileClient.getInstance().tokens.idToken.tokenString}"
            return map
        }

}