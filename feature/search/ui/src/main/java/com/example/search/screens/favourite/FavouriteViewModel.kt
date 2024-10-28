package com.example.search.screens.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetRecipesFromLocalDbUseCase
import com.example.search.screens.favourite.FavouriteScreen.Navigation.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val getRecipesFromLocalDbUseCase: GetRecipesFromLocalDbUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(FavouriteScreen.UiState())
    val uiState: Flow<FavouriteScreen.UiState> get() = _uiState.asStateFlow()

    private var originalList = mutableListOf<Recipe>()
    private var _navigation = Channel<FavouriteScreen.Navigation>()
    val navigation: Flow<FavouriteScreen.Navigation> get() = _navigation.receiveAsFlow()

    init {
        getRecipeList()
    }

    fun onEvent(event: FavouriteScreen.Event) {
        when (event) {
            FavouriteScreen.Event.AlphabeticalSort -> alphabeticalSort()
            FavouriteScreen.Event.LessIngredientsSort -> lessIngredientsSort()
            FavouriteScreen.Event.ResetSort -> resetSort()
            is FavouriteScreen.Event.ShowRecipeDetails -> {
                viewModelScope.launch {
                    _navigation.send(GoToRecipeDetailsScreen(event.id))
                }
            }

            is FavouriteScreen.Event.DeleteRecipe -> {
                deleteRecipe(event.recipe)
            }

            is FavouriteScreen.Event.GoToRecipeDetails -> {
                viewModelScope.launch {
                    _navigation.send(GoToRecipeDetailsScreen(event.id))
                }
            }

           is FavouriteScreen.Event.GoToRecipeListScreen -> {
                viewModelScope.launch{
                    _navigation.send(GoToRecipeListScreen)
                }
            }
        }
    }

    private fun getRecipeList() {
        viewModelScope.launch {
            getRecipesFromLocalDbUseCase.invoke().collectLatest { list ->
                originalList = list.toMutableList()
                _uiState.update { FavouriteScreen.UiState(data = list) }
            }
        }
    }

    fun alphabeticalSort() =
        _uiState.update { FavouriteScreen.UiState(data = originalList.sortedBy { it.strMeal }) }

    fun lessIngredientsSort() =
        _uiState.update { FavouriteScreen.UiState(data = originalList.sortedBy { it.strInstructions.length }) }

    fun resetSort() = _uiState.update { FavouriteScreen.UiState(data = originalList) }

    fun deleteRecipe(recipe: Recipe) = deleteRecipeUseCase.invoke(recipe).launchIn(viewModelScope)
}

object FavouriteScreen {
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: List<Recipe>? = null
    )

    sealed interface Navigation {
        data class GoToRecipeDetailsScreen(val id: String) : Navigation
        data object GoToRecipeListScreen : Navigation
    }

    sealed interface Event {
        data object AlphabeticalSort : Event
        data object LessIngredientsSort : Event
        data object ResetSort : Event
        data object GoToRecipeListScreen : Event

        data class ShowRecipeDetails(val id: String) : Event
        data class DeleteRecipe(val recipe: Recipe) : Event
        data class GoToRecipeDetails(val id: String) : Event
    }
}