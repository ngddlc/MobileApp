package com.example.collegeschedule.data.api

import retrofit2.http.GET

interface GroupApi {
    @GET("api/groups")
    suspend fun getAllGroups(): List<String>
}