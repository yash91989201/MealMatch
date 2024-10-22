package com.example.search.screens.recipe_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.GetRecipeDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import jakarta.inject.Inject

class RecipeDetailsViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(com.example.search.screens.recipe_details.RecipeDetails.UiState())
    val uiState: StateFlow<com.example.search.screens.recipe_details.RecipeDetails.UiState> get() = _uiState.asStateFlow()

    fun onEvent(event: com.example.search.screens.recipe_details.RecipeDetails.Event) {
        when (event) {
            is com.example.search.screens.recipe_details.RecipeDetails.Event.FetchRecipeDetails -> {
                recipeDetails(event.id)
            }
        }
    }

    fun recipeDetails(id: String) = getRecipeDetailsUseCase.invoke(id).onEach { result ->
        when (result) {
            is NetworkResult.Loading -> {
                _uiState.update {
                    com.example.search.screens.recipe_details.RecipeDetails.UiState(
                        isLoading = true
                    )
                }
            }

            is NetworkResult.Success -> {
                _uiState.update {
                    com.example.search.screens.recipe_details.RecipeDetails.UiState(
                        data = result.data,
                    )
                }
            }

            is NetworkResult.Error -> {
                _uiState.update {
                    com.example.search.screens.recipe_details.RecipeDetails.UiState(
                        error = UiText.RemoteString(result.message.toString())
                    )
                }
            }
        }
    }.launchIn(viewModelScope)
}

object RecipeDetails {
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: RecipeDetails? = null
    )

    sealed interface Navigation {}

    sealed interface Event {
        data class FetchRecipeDetails(val id: String)
    }
}