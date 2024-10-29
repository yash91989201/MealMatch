package com.example.media_player.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoute
import com.example.common.navigation.NavigationSubGraphRoute
import com.example.media_player.screens.MediaPlayerScreen

interface MediaPlayerFeatureApi : FeatureApi {}

class MediaPlayerImpl : MediaPlayerFeatureApi {
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.MediaPlayer.route,
            startDestination = NavigationRoute.MediaPlayer.route
        ) {
            composable(route = NavigationRoute.MediaPlayer.route) {
                val videoId = it.arguments?.getString("video_id")
                videoId?.let {
                    MediaPlayerScreen(videoId = it)
                }
            }
        }
    }
}