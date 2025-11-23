package com.example.weatherappcompose.di

import android.content.Context
import com.example.weatherappcompose.data.impl.WeatherRepositoryImpl
import com.example.weatherappcompose.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext context: Context): WeatherRepository{
        return WeatherRepositoryImpl(context)
    }
}