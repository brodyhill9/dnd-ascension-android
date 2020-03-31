package com.example.dndascension.utils

import com.amazonaws.mobile.client.AWSMobileClient
import com.android.volley.Request

sealed class ApiRoute {
    data class GetSpells(val placeholder: String = "") : ApiRoute()
    data class GetSpell(var id: Int) : ApiRoute()

    val timeOut: Int
        get() {
            return 3000
        }

    val url: String
        get() {
            return when (this) {
                is GetSpell, is GetSpells -> "https://ctctjf95w6.execute-api.us-east-2.amazonaws.com/dev"
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
                is GetSpell -> {
                    map["spellId"] = this.id.toString()
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