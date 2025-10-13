package com.example.weatherappcompose.data.impl

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappcompose.API_KEY
import com.example.weatherappcompose.domain.model.WeatherModel
import com.example.weatherappcompose.domain.repository.WeatherRepository
import org.json.JSONObject

class WeatherRepositoryImpl(private val context: Context) : WeatherRepository {

    override fun getWeatherData(
        city: String,
        onSuccess: (List<WeatherModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "https://api.weatherapi.com/v1/forecast.json" +
                "?key=$API_KEY&" +
                "q=$city" +
                "&days=7" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val weatherList = parseWeatherResponse(response)
                    onSuccess(weatherList)
                } catch (e: Exception) {
                    onError("Ошибка парсинга данных getWeatherData ${e.message}")
                }
            },
            { error ->
                onError("Ошибка сети: ${error.message}")
            }
        )
        queue.add(stringRequest)
    }

    private fun parseWeatherResponse(response: String): List<WeatherModel>{

        if (response.isEmpty()) return listOf()
        val list = ArrayList<WeatherModel>()
        val mainObject = JSONObject(response)

        val city = mainObject.getJSONObject("location").getString("name")
        val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

        for (i in 0 until days.length()) {
            val item = days[i] as JSONObject
            list.add(
                WeatherModel(
                    city,
                    item.getString("date"),
                    currentTemp = "",
                    item
                        .getJSONObject("day")
                        .getJSONObject("condition")
                        .getString("text"),
                    item
                        .getJSONObject("day")
                        .getJSONObject("condition")
                        .getString("icon"),
                    item
                        .getJSONObject("day")
                        .getString("maxtemp_c"),
                    item
                        .getJSONObject("day")
                        .getString("mintemp_c"),
                    item.getJSONArray("hour").toString(),
                    item.getJSONObject("day")
                        .getString("uv")
                )
            )
        }
        list[0] = list[0].copy(
            time = mainObject.getJSONObject("current").getString("last_updated"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c")
        )
        return list
    }
}