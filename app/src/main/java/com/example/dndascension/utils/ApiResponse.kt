package com.example.dndascension.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class ApiResponse(response: String) {
    private val TAG = this::class.java.simpleName

    var success: Boolean = false
    var message: String = ""
    var json: String = ""

    private val data = "data"
    private val msg = "message"
    private val result = "result"

    init {
        try {
            val jsonToken = JSONTokener(response).nextValue()
            Log.i(TAG, jsonToken.toString())
            message = "An error has occurred while processing the response"
            success = false
            if (jsonToken is JSONObject) {
                val jsonResponse = JSONObject(response)
                if (jsonResponse.has(msg)) {
                    message = jsonResponse.get(msg).toString()
                } else if (jsonResponse.has(result)) {
                    message = jsonResponse.get(result).toString()
                    success = true
                } else if (jsonResponse != null) {
                    json = jsonResponse.toString()
                    success = true
                }

            } else if (jsonToken is JSONArray) {
                val jsonResponse = JSONArray(response)
                if (jsonResponse != null) {
                    json = jsonResponse.toString()
                    success = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}