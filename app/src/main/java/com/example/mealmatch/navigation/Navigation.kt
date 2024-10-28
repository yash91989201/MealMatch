package com.example.mealmatch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.NavigationSubGraphRoute

@Composable
fun RecipeNavigation(navigationSubGraphs: NavigationSubGraphs) {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = NavigationSubGraphRoute.Search.route
    ) {
        navigationSubGraphs.searchFeatureApi.registerGraph(
            navHostController = navHostController,
            navGraphBuilder = this,
        )

        navigationSubGraphs.mediaPlayerFeatureApi.registerGraph(
            navHostController = navHostController,
            navGraphBuilder = this
        )
    }
}