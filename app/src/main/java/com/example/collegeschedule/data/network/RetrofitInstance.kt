package com.example.collegeschedule.data.network

import com.example.collegeschedule.data.api.GroupApi
import com.example.collegeschedule.data.api.ScheduleApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5065/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ScheduleApi by lazy { retrofit.create(ScheduleApi::class.java) }
    val groupApi: GroupApi by lazy { retrofit.create(GroupApi::class.java) }
}