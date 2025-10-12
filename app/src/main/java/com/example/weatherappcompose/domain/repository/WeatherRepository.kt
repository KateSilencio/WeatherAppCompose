package com.example.weatherappcompose.domain.repository

import com.example.weatherappcompose.domain.model.WeatherModel

interface WeatherRepository {

    fun getWeatherData(
        city: String,
        onSuccess: (List<WeatherModel>) -> Unit,
        onError: (String) -> Unit)

}