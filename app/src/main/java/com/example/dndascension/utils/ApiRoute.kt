package com.example.dndascension.utils

import com.amazonaws.mobile.client.AWSMobileClient
import com.android.volley.Request
import com.example.dndascension.interfaces.SetValue
import com.example.dndascension.interfaces.serializeToMap
import com.example.dndascension.models.Spell

sealed class ApiRoute {
    data class GetSetValues(var setName: String) : ApiRoute()
    data class SaveSetValue(var setValue: SetValue) : ApiRoute()
    data class DeleteSetValue(var valueId: Int) : ApiRoute()

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
                is GetSetValues -> "https://8ou4ni3fbg.execute-api.us-east-2.amazonaws.com/dev?set_name=${this.setName}"
                is SaveSetValue -> "https://8ou4ni3fbg.execute-api.us-east-2.amazonaws.com/dev"
                is DeleteSetValue -> "https://8ou4ni3fbg.execute-api.us-east-2.amazonaws.com/dev?value_id=${this.valueId}"
                is GetSpells, is SaveSpell -> "https://ctctjf95w6.execute-api.us-east-2.amazonaws.com/dev"
                is GetSpell -> "https://ctctjf95w6.execute-api.us-east-2.amazonaws.com/dev?spell_id=${this.id}"
            }
        }
    val httpMethod: Int
        get() {
            return when (this) {
                is SaveSetValue -> if (this.setValue.isNew()) Request.Method.POST else Request.Method.PUT
                is DeleteSetValue -> Request.Method.DELETE
                else -> Request.Method.GET
            }
        }

    val params: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            when (this) {
                is SaveSetValue -> {
                    return this.setValue.serializeToMap()
                }
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