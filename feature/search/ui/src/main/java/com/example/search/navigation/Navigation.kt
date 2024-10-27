package com.example.search.navigation

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoutes
import com.example.common.navigation.NavigationSubGraphRoute
import com.example.search.screens.favourite.FavouriteScreen
import com.example.search.screens.favourite.FavouriteViewModel
import com.example.search.screens.recipe_details.RecipeDetails
import com.example.search.screens.recipe_details.RecipeDetailsScreen
import com.example.search.screens.recipe_details.RecipeDetailsViewModel
import com.example.search.screens.recipe_list.RecipeList
import com.example.search.screens.recipe_list.RecipeListScreen
import com.example.search.screens.recipe_list.RecipeListViewModel

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navHostController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoutes.RecipeList.route
        ) {
            composable(route = NavigationRoutes.RecipeList.route) {
                val viewModel = hiltViewModel<RecipeListViewModel>()
                RecipeListScreen(
                    viewModel = viewModel,
                    navHostController = navHostController
                ) { mealId ->
                    viewModel.onEvent(RecipeList.Event.GoToRecipeDetails(mealId))
                }
            }
            composable(route = NavigationRoutes.RecipeDetails.route) {
                val mealId = it.arguments?.getString("id")
                val viewModel = hiltViewModel<RecipeDetailsViewModel>()

                LaunchedEffect(key1 = mealId) {
                    mealId?.let {
                        viewModel.onEvent(RecipeDetails.Event.FetchRecipeDetails(id = it))
                    }
                }

                RecipeDetailsScreen(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    onNavigationClick = {
                        viewModel.onEvent(RecipeDetails.Event.GoToRecipeListScreen)
                    },
                    onFavouriteClick = {
                        viewModel.onEvent(RecipeDetails.Event.InsertRecipe(it))
                    },
                    onDelete = {
                        viewModel.onEvent(RecipeDetails.Event.DeleteRecipe(it))
                    },
                )
            }
            composable(route = NavigationRoutes.FavouriteScreen.route) {
                val viewModel = hiltViewModel<FavouriteViewModel>()
                FavouriteScreen(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    onClick = { mealId ->
                        viewModel.onEvent(FavouriteScreen.Event.GoToRecipeDetails(mealId))
                    }
                )
            }
        }
    }
}