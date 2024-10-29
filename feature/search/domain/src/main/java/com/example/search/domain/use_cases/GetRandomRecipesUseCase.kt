package com.example.search.domain.use_cases

import com.example.common.utils.NetworkResult
import com.example.search.domain.model.Recipe
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetRandomRecipesUseCase @Inject constructor(val searchRepository: SearchRepository) {
    operator fun invoke(q: String) = flow<NetworkResult<List<Recipe>>> {
        emit(NetworkResult.Loading())
        val response = searchRepository.getRandomRecipes(q)
        if (response.isSuccess) {
            emit(NetworkResult.Success(data = response.getOrThrow()))
        } else {
            emit(NetworkResult.Error(message = response.exceptionOrNull()?.localizedMessage))
        }
    }.catch {
        emit(NetworkResult.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}