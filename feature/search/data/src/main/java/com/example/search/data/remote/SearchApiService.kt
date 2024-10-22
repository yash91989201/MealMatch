package com.example.search.data.remote

import com.example.search.data.model.RecipeDetailsResponse
import com.example.search.data.model.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("/api/json/v1/1/search.php")
    suspend fun getRecepies(
        @Query("s") s: String
    ): Response<RecipeResponse>

    @GET("/api/json/v1/1/search.php")
    suspend fun getRecepieDetails(
        @Query("i") i: String
    ): Response<RecipeDetailsResponse>
}