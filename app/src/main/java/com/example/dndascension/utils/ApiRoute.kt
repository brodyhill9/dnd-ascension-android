package com.example.dndascension.utils

import com.amazonaws.mobile.client.AWSMobileClient
import com.android.volley.Request
import com.example.dndascension.interfaces.SetValue
import com.example.dndascension.models.*

sealed class ApiRoute {
    data class GetCharacters(val placeholder: String = "") : ApiRoute()
    data class GetCharacter(val id: Int) : ApiRoute()
    data class SaveCharacter(var character: Character) : ApiRoute()
    data class DeleteCharacter(var id: Int) : ApiRoute()

    data class GetArmor(val placeholder: String = "") : ApiRoute()
    data class SaveArmor(var armor: Armor) : ApiRoute()
    data class DeleteArmor(var id: Int) : ApiRoute()

    data class GetSetValues(var setName: String) : ApiRoute()
    data class SaveSetValue(var setValue: SetValue) : ApiRoute()
    data class DeleteSetValue(var id: Int) : ApiRoute()

    data class GetClasses(val placeholder: String = "") : ApiRoute()
    data class SaveClass(var cls: DndClass) : ApiRoute()
    data class DeleteClass(var id: Int) : ApiRoute()

    data class GetRaces(val placeholder: String = "") : ApiRoute()
    data class GetRace(val id: Int) : ApiRoute()
    data class SaveRace(var race: Race) : ApiRoute()
    data class DeleteRace(var id: Int) : ApiRoute()

    data class GetSpells(val placeholder: String = "") : ApiRoute()
    data class SaveSpell(var spell: Spell) : ApiRoute()
    data class DeleteSpell(var id: Int) : ApiRoute()

    data class GetWeapons(val placeholder: String = "") : ApiRoute()
    data class SaveWeapon(var weapon: Weapon) : ApiRoute()
    data class DeleteWeapon(var id: Int) : ApiRoute()

    val timeOut: Int
        get() {
            return 3000
        }

    val url: String
        get() {
            return when (this) {
                is GetCharacters, is SaveCharacter -> "https://b2dgyjylr0.execute-api.us-east-2.amazonaws.com/dev"
                is GetCharacter -> "https://b2dgyjylr0.execute-api.us-east-2.amazonaws.com/dev?char_id=${this.id}"
                is DeleteCharacter -> "https://b2dgyjylr0.execute-api.us-east-2.amazonaws.com/dev?char_id=${this.id}"

                is GetArmor, is SaveArmor -> "https://oktiap020h.execute-api.us-east-2.amazonaws.com/dev"
                is DeleteArmor -> "https://oktiap020h.execute-api.us-east-2.amazonaws.com/dev?armor_id=${this.id}"

                is GetSetValues -> "https://8ou4ni3fbg.execute-api.us-east-2.amazonaws.com/dev?set_name=${this.setName}"
                is SaveSetValue -> "https://8ou4ni3fbg.execute-api.us-east-2.amazonaws.com/dev"
                is DeleteSetValue -> "https://8ou4ni3fbg.execute-api.us-east-2.amazonaws.com/dev?value_id=${this.id}"

                is GetClasses, is SaveClass -> "https://rtei0ksjjb.execute-api.us-east-2.amazonaws.com/dev"
                is DeleteClass -> "https://rtei0ksjjb.execute-api.us-east-2.amazonaws.com/dev?class_id=${this.id}"

                is GetRaces, is SaveRace -> "https://0o2cs3p5c6.execute-api.us-east-2.amazonaws.com/dev"
                is GetRace -> "https://0o2cs3p5c6.execute-api.us-east-2.amazonaws.com/dev?race_id=${this.id}"
                is DeleteRace -> "https://0o2cs3p5c6.execute-api.us-east-2.amazonaws.com/dev?race_id=${this.id}"

                is GetSpells, is SaveSpell -> "https://ctctjf95w6.execute-api.us-east-2.amazonaws.com/dev"
                is DeleteSpell -> "https://ctctjf95w6.execute-api.us-east-2.amazonaws.com/dev?spell_id=${this.id}"

                is GetWeapons, is SaveWeapon -> "https://y9hn36s0h5.execute-api.us-east-2.amazonaws.com/dev"
                is DeleteWeapon -> "https://y9hn36s0h5.execute-api.us-east-2.amazonaws.com/dev?weapon_id=${this.id}"
            }
        }
    val httpMethod: Int
        get() {
            return when (this) {
                is SaveCharacter-> if (this.character.isNew()) Request.Method.POST else Request.Method.PUT
                is SaveArmor -> if (this.armor.isNew()) Request.Method.POST else Request.Method.PUT
                is SaveSetValue -> if (this.setValue.isNew()) Request.Method.POST else Request.Method.PUT
                is SaveClass -> if (this.cls.isNew()) Request.Method.POST else Request.Method.PUT
                is SaveRace -> if (this.race.isNew()) Request.Method.POST else Request.Method.PUT
                is SaveSpell -> if (this.spell.isNew()) Request.Method.POST else Request.Method.PUT
                is SaveWeapon -> if (this.weapon.isNew()) Request.Method.POST else Request.Method.PUT

                is DeleteCharacter,
                is DeleteArmor,
                is DeleteSetValue,
                is DeleteClass,
                is DeleteRace,
                is DeleteSpell,
                is DeleteWeapon -> Request.Method.DELETE

                else -> Request.Method.GET
            }
        }

    val params: String
        get() {
            when (this) {
                is SaveCharacter -> {
                    return this.character.toJSON()
                }
                is SaveArmor -> {
                    return this.armor.toJSON()
                }
                is SaveSetValue -> {
                    return this.setValue.toJSON()
                }
                is SaveClass -> {
                    return this.cls.toJSON()
                }
                is SaveRace -> {
                    return this.race.toJSON()
                }
                is SaveSpell -> {
                    return this.spell.toJSON()
                }
                is SaveWeapon -> {
                    return this.weapon.toJSON()
                }
            }
            return ""
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