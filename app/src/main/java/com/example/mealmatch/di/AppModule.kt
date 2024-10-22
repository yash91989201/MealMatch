package com.example.mealmatch.di

import com.example.mealmatch.navigation.NavigationSubGraphs
import com.example.search.navigation.SearchFeatureApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideNavigationSubGraphs(searchFeatureApi: SearchFeatureApi): NavigationSubGraphs{
        return NavigationSubGraphs(searchFeatureApi)
    }
}