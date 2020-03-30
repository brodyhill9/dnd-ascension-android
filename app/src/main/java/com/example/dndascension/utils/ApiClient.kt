package com.example.dndascension.utils

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest

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
            override fun getParams(): MutableMap<String, String> {
                return route.params
            }

            override fun getHeaders(): MutableMap<String, String> {
                return route.headers
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

}