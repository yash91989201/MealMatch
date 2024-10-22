package com.example.search.di

import com.example.search.navigation.SearchFeatureApi
import com.example.search.navigation.SearchFeatureApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object UiModule {
    @Provides
    fun provideSearchFeatureApi(): SearchFeatureApi{
        return SearchFeatureApiImpl()
    }
}