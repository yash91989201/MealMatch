package com.example.mealmatch.di

import android.content.Context
import com.example.mealmatch.local.AppDatabase
import com.example.mealmatch.navigation.NavigationSubGraphs
import com.example.media_player.navigation.MediaPlayerFeatureApi
import com.example.search.data.local.RecipeDao
import com.example.search.navigation.SearchFeatureApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideNavigationSubGraphs(searchFeatureApi: SearchFeatureApi, mediaPlayerFeatureApi: MediaPlayerFeatureApi): NavigationSubGraphs {
        return NavigationSubGraphs(searchFeatureApi, mediaPlayerFeatureApi)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Provides
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao{
        return appDatabase.getRecipeDao()
    }
}