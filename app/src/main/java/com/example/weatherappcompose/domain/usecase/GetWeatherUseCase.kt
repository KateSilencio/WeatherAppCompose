package com.example.weatherappcompose.domain.usecase

import com.example.weatherappcompose.domain.model.WeatherModel
import com.example.weatherappcompose.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(private val repository: WeatherRepository) {

    // Используем колбеки
    fun getWeather(
        city: String,
        onSuccess: (List<WeatherModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        repository.getWeatherData(city, onSuccess, onError)
    }
}