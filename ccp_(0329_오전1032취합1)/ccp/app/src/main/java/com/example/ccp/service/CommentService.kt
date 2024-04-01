package com.example.ccp.service

import android.graphics.ColorSpace.Model
import com.example.ccp.model.CommentDTO
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CommentService {
    // 댓글창 출력
    @GET("/comments/{num}")
    fun getAllComments() : Call<List<CommentDTO>>

    // 댓글 작성
    @POST("/comments/{boardNum}")
    fun addComments(@Body commentDTO: CommentDTO): Call<Void>

    // 댓글 수정
    @PUT("/comments/{cnum}")
    fun updateComments(@Path("cnum") cnum: Int, @Body requestBody: CommentDTO): Call<CommentDTO>

    // 댓글 삭제
    @DELETE("/comments/{cnum}")
    fun deleteComments(@Path("cnum") cnum: Int): Call<Void>
}