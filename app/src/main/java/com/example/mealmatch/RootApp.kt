package com.example.mealmatch

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mealmatch.navigation.NavigationSubGraphs
import com.example.mealmatch.navigation.RecipeNavigation

@Composable
fun RootApp(navigationSubGraphs: NavigationSubGraphs) {
    Surface(modifier = Modifier.systemBarsPadding()){
        RecipeNavigation(navigationSubGraphs = navigationSubGraphs)
    }
}