package com.apptest.services.endpoints

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface Services {

    @FormUrlEncoded
    @POST("auth/oauth/v2/token")
    fun getOAuthToken(
        @Header("Authorization") authorization: String,
        @Field("scope") scope: String,
        @Field("grant_type") grantType: String,
        @Header("channel") channel: String = "checkout-android"
    ): Call<JsonObject>
}