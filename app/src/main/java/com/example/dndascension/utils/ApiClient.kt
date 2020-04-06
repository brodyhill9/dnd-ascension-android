package com.example.dndascension.utils

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.example.dndascension.interfaces.fromJson
import com.example.dndascension.models.*
import com.google.gson.Gson

class ApiClient(private val ctx: Context) {
    private fun performRequest(route: ApiRoute, completion: (success: Boolean, apiResponse: ApiResponse) -> Unit) {
        val request: StringRequest = object : StringRequest(route.httpMethod, route.url, { response ->
            this.handle(response, completion)
        }, {
            it.printStackTrace()
            if (it.networkResponse != null && it.networkResponse.data != null)
                this.handle(String(it.networkResponse.data), completion)
            else
                this.handle(getStringError(it), completion)
        }) {
//            override fun getParams(): HashMap<String, String> {
//                return route.params
//            }

            override fun getHeaders(): MutableMap<String, String> {
                return route.headers
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                return Gson().toJson(route.params).toString().toByteArray()
            }
        }
        request.retryPolicy = DefaultRetryPolicy(route.timeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        getRequestQueue().add(request)
    }

    private fun handle(response: String, completion: (success: Boolean, apiResponse: ApiResponse) -> Unit) {
        val ar = ApiResponse(response)
        completion.invoke(ar.success, ar)
    }

    private fun getStringError(volleyError: VolleyError): String {
        return when (volleyError) {
            is TimeoutError -> "The connection timed out."
            is NoConnectionError -> "The connection couldn´t be established."
            is AuthFailureError -> "There was an authentication failure in your request."
            is ServerError -> "Error while processing the server response."
            is NetworkError -> "Network error, please verify your connection."
            is ParseError -> "Error while processing the server response."
            else -> "Internet error"
        }
    }

    private fun getRequestQueue(): RequestQueue {
        val maxCacheSize = 20 * 1024 * 1024
        val cache = DiskBasedCache(ctx.cacheDir, maxCacheSize)
        val netWork = BasicNetwork(HurlStack())
        val mRequestQueue = RequestQueue(cache, netWork)
        mRequestQueue.start()
        System.setProperty("http.keepAlive", "false")
        return mRequestQueue
    }

    fun getArmor(result: (armor: List<Armor>?, message: String) -> Unit) {
        val route = ApiRoute.GetArmor()
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun saveArmor(armor: Armor, result: (armor: Armor?, message: String) -> Unit) {
        val route = ApiRoute.SaveArmor(armor)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun deleteArmor(id: Int, result: (error: Boolean, message: String) -> Unit) {
        val route = ApiRoute.DeleteArmor(id)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(false, response.message)
            } else {
                result.invoke(true, response.message)
            }
        }
    }

    fun getBackgrounds(result: (backgrounds: List<Background>?, message: String) -> Unit) {
        val route = ApiRoute.GetSetValues("Backgrounds")
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun saveBackground(background: Background, result: (background: Background?, message: String) -> Unit) {
        val route = ApiRoute.SaveSetValue(background)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun deleteBackground(id: Int, result: (error: Boolean, message: String) -> Unit) {
        val route = ApiRoute.DeleteSetValue(id)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(false, response.message)
            } else {
                result.invoke(true, response.message)
            }
        }
    }

    fun getFeats(result: (feats: List<Feat>?, message: String) -> Unit) {
        val route = ApiRoute.GetSetValues("Feats")
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun saveFeat(feat: Feat, result: (feat: Feat?, message: String) -> Unit) {
        val route = ApiRoute.SaveSetValue(feat)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun deleteFeat(id: Int, result: (error: Boolean, message: String) -> Unit) {
        val route = ApiRoute.DeleteSetValue(id)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(false, response.message)
            } else {
                result.invoke(true, response.message)
            }
        }
    }

    fun getSpells(result: (spells: List<Spell>?, message: String) -> Unit) {
        val route = ApiRoute.GetSpells()
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun saveSpell(spell: Spell, result: (spell: Spell?, message: String) -> Unit) {
        val route = ApiRoute.SaveSpell(spell)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun deleteSpell(id: Int, result: (error: Boolean, message: String) -> Unit) {
        val route = ApiRoute.DeleteSpell(id)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(false, response.message)
            } else {
                result.invoke(true, response.message)
            }
        }
    }

    fun getWeapons(result: (weapons: List<Weapon>?, message: String) -> Unit) {
        val route = ApiRoute.GetWeapons()
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun saveWeapon(weapon: Weapon, result: (weapon: Weapon?, message: String) -> Unit) {
        val route = ApiRoute.SaveWeapon(weapon)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(response.json.fromJson(), "")
            } else {
                result.invoke(null, response.message)
            }
        }
    }
    fun deleteWeapon(id: Int, result: (error: Boolean, message: String) -> Unit) {
        val route = ApiRoute.DeleteWeapon(id)
        performRequest(route) { success, response ->
            if (success) {
                result.invoke(false, response.message)
            } else {
                result.invoke(true, response.message)
            }
        }
    }
}