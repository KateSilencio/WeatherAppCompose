package com.example.weatherappcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappcompose.MainActivity
import com.example.weatherappcompose.data.impl.WeatherRepositoryImpl
import com.example.weatherappcompose.domain.model.WeatherModel
import com.example.weatherappcompose.domain.usecase.GetWeatherUseCase
import com.example.weatherappcompose.screen.DialogSearch
import com.example.weatherappcompose.screen.MainCard
import com.example.weatherappcompose.screen.TabLayout
import org.json.JSONObject

const val API_KEY = "080e76a8ce88459eba7144015252009"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = WeatherRepositoryImpl(this)
        val getWeatherUseCase = GetWeatherUseCase(repository)

        setContent {

            WeatherApp(getWeatherUseCase)


//            if (dialogState.value){
//                DialogSearch(dialogState, onSubmit = {
//                    getData(it, this, daysList, currantDay)
//                })
//            }
//            getData("Moscow", this, daysList, currantDay)


//
        }
    }
}

@Composable
fun WeatherApp(getWeatherUseCase: GetWeatherUseCase) {
    var daysList = remember { mutableStateOf(emptyList<WeatherModel>()) }
    var dialogState = remember { mutableStateOf(false) }
    var currantDay = remember {
        mutableStateOf(
            WeatherModel(
                "",
                "",
                "0",
                "",
                "",
                "0",
                "0",
                "",
                "0"
            )
        )
    }

    // Фон приложения
    Image(
        painter = painterResource(id = R.drawable.sky_background_img),
        contentDescription = "img_background",
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.7f),
        contentScale = ContentScale.FillBounds
    )
// Загрузка данных при первом запуске
    LaunchedEffect(Unit) {
        loadWeatherData("Moscow", getWeatherUseCase) { newDaysList ->
            daysList.value = newDaysList
            if (newDaysList.isNotEmpty()) {
                currantDay.value = newDaysList[0]
            }
        }
    }
    Column {
        MainCard(
            currentDay = currantDay,
            onClickSync = {
                loadWeatherData("Moscow", getWeatherUseCase) { newDaysList ->
                    daysList.value = newDaysList
                    if (newDaysList.isNotEmpty()) {
                        currantDay.value = newDaysList[0]
                    }
                }
            },
            onClickSearch = {
                dialogState.value = true
            })
        TabLayout(
            daysList = daysList,
            currentDay = currantDay
        )
    }

    // Диалог поиска
    if (dialogState.value) {
        DialogSearch(
            dialogState = dialogState,
            onSubmit = { city ->
                loadWeatherData(city, getWeatherUseCase) { newDaysList ->
                    daysList.value = newDaysList
                    if (newDaysList.isNotEmpty()) {
                        currantDay.value = newDaysList[0]
                    }
                }
                dialogState.value = false
            })
    }
}

private fun loadWeatherData(
    city: String,
    getWeatherUseCase: GetWeatherUseCase,
    onSuccess: (List<WeatherModel>) -> Unit
){
    getWeatherUseCase.getWeather(
        city = city,
        onSuccess = onSuccess,
        onError = { error ->
            Log.e("Error", "Ошибка загрузки ${error}")
        })
}


