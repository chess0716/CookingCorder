package com.example.ccp.util

import com.example.ccp.service.ApiService
import com.example.ccp.service.IngrService
import com.example.ccp.service.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.100.103.73:8005/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val ingrService: IngrService by lazy {
        retrofit.create(IngrService::class.java)
    }
}
