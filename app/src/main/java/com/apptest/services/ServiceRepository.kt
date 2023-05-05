package com.apptest.services

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ServiceRepository {

    fun getAccessToken(baseUrl: String, oauth: String, callback: (String, String) -> Unit) {
        ServicesAPI.services(baseUrl)
            .getOAuthToken(
                "Basic $oauth",
                "oob",
                "client_credentials"
            ).enqueue(object : Callback<JsonObject> {

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    createFailureLog(t) { json ->
                        callback("", json.toString())
                    }
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val errorBodyString = response.errorBody()?.string()
                    if (response.isSuccessful) {
                        response.body()?.get("access_token")?.let {
                            callback(it.asString, "")
                        }
                    } else {
                        if (errorBodyString != null) {
                            callback("", errorBodyString)
                        }
                    }
                }
            })
    }

    private fun createFailureLog(t: Throwable, callback: (JsonObject) -> Unit) {
        val json = JsonObject()
        json.addProperty("error", t.message)
        callback(json)
    }
}