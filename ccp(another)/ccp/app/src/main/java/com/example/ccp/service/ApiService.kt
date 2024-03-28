package com.example.ccp.service

import com.example.ccp.model.BoardDTO
import com.example.ccp.model.Category
import com.example.ccp.model.LoginRequest
import com.example.ccp.model.LoginResponse
import com.example.ccp.model.User
import com.example.ccp.model.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService : UserService {
    @GET("/api/boards/list")
    fun getAllBoards(): Call<List<BoardDTO>>

    @GET
    fun getImage(@Url imageUrl: String): Call<ResponseBody>

    @GET("/api/boards/{num}")
    fun getBoardByNum(@Path("num") num: Int): Call<BoardDTO?>?

    @POST("/api/boards")
    fun insertBoard(@Body boardDTO: BoardDTO?): Call<Void?>?

    @POST("/api/boards/{num}/calculatePrice")
    fun updatePrice(@Path("num") boardNum: Int, @Body requestBody: Map<String?, Any?>?): Call<Int?>?

    @POST("/api/join")
    override fun join(@Body user: User?): Call<UserResponse?>?

    @POST("/api/login")
    override fun login(@Body loginRequest: LoginRequest?): Call<LoginResponse?>?
    @GET("/api/boards/search")
    fun searchBoards(@Query("title") title: String): Call<List<BoardDTO>>


}
