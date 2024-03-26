package com.example.ccp.service

import com.example.ccp.model.BoardDTO
import com.example.ccp.model.DataDTO
import com.example.ccp.model.IngrBoard
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface IngrService {

    @GET("/get_names")
    fun getNamesByCategory(@Query("categoryId") categoryId: String): Call<List<DataDTO>>
    @POST("/api/submit_all_forms")
    fun submitAllForms(@Body forms: List<IngrBoard>): Call<String>

    @Multipart
    @POST("/api/submit_recipe")
    fun submitRecipe(
        @Part("title") title: String,
        @Part("content") content: String,
        @Part file: MultipartBody.Part
    ): Call<String>

    @POST("/api/submit_all_forms-update")
    fun submitAllFormsUpdate(@Body ingredientForms: List<IngrBoard>): Call<Void>

    @PUT("/api/submit_recipe_update")
    fun submitRecipeUpdate(@Body recipeForm: BoardDTO): Call<Void>

    @DELETE("/api/delete/{num}")
    fun deleteRecipe(@Path("num") num: Int, @Body requestBody: Map<String, String>): Call<Int>
}
