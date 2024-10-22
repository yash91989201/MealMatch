package com.example.search.data.repository

import com.example.search.data.mappers.toDomain
import com.example.search.data.remote.SearchApiService
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository

class SearchRepoImpl(
    private val searchApiService: SearchApiService,
) : SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return try {
            val response = searchApiService.getRecepies(s)
            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    Result.success(it.toDomain())
                } ?: run { Result.failure(Exception("Error Occurred: Fetching recipes")) }
            } else {
                Result.failure(Exception("Error Occurred: Fetching recipes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
       return try {
           val response = searchApiService.getRecepieDetails(id)
           if (response.isSuccessful) {
               response.body()?.meals?.let {
                   if (it.isNotEmpty()) {
                       Result.success(it.first().toDomain())
                   } else {
                       Result.failure(Exception("Unable to get recipe details"))
                   }
               } ?: run {
                   Result.failure(Exception("Unable to get recipe details"))
               }
           } else {
               Result.failure(Exception("Unable to get recipe details"))
           }
       }catch (e: Exception){
           Result.failure(e)
       }
    }
}