package com.example.dndascension.utils

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class ApiResponse(response: String) {

    var success: Boolean = false
    var message: String = ""
    var json: String = ""

    private val data = "data"
    private val msg = "message"

    init {
        try {
            val jsonToken = JSONTokener(response).nextValue()
            if (jsonToken is JSONObject) {
                val jsonResponse = JSONObject(response)

                message = if (jsonResponse.has(msg)) {
                    jsonResponse.get(msg).toString()
                } else {
                    "An error has occurred while processing the response"
                }

                if (jsonResponse.optJSONObject(data) != null) {
                    json = jsonResponse.getJSONObject(data).toString()
                    success = true
                } else {
                    success = false
                }

            } else if (jsonToken is JSONArray) {
                val jsonResponse = JSONArray(response)
                if (jsonResponse != null) {
                    json = jsonResponse.toString()
                    success = true
                } else {
                    success = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}