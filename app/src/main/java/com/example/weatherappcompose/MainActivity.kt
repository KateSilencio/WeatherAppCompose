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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.weatherappcompose.data.WeatherModel
import com.example.weatherappcompose.screens.DialogSearch
import com.example.weatherappcompose.screens.MainCard
import com.example.weatherappcompose.screens.TabLayout
import org.json.JSONObject

const val API_KEY = "080e76a8ce88459eba7144015252009"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //Greeting(name = "Moscow",this)

            val daysList = remember {
                mutableStateOf(listOf<WeatherModel>())
            }
            val dialogState = remember {
                mutableStateOf(false)
            }
            val currantDay = remember {
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
            if (dialogState.value){
                DialogSearch(dialogState, onSubmit = {
                    getData(it, this, daysList, currantDay)
                })
            }
            getData("Moscow", this, daysList, currantDay)
            Image(
                painter = painterResource(id = R.drawable.sky_background_img),
                contentDescription = "img_background",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.7f),
                contentScale = ContentScale.FillBounds
            )

            Column {
                MainCard(
                    currantDay,
                    onClickSync = {
                        getData("Moscow", this@MainActivity, daysList, currantDay)
                    },
                    onClickSearch = {
                        dialogState.value = true
                    })
                TabLayout(daysList, currantDay)
            }

        }
    }
}


@Composable
fun Greeting(name: String, context: Context) {
    val stateWeather = remember {
        mutableStateOf("Unknown")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                fontSize = 18.sp,
                text = "Temperature in $name! = ${stateWeather.value} C"
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    getResult(name, stateWeather, context)
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Refresh")
            }
        }
    }
}

//старая функция
private fun getResult(city: String, state: MutableState<String>, context: Context) {
    val url = "https://api.weatherapi.com/v1/current.json" +
            "?key=$API_KEY&" +
            "q=$city" +
            "&aqi=no"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val obj = JSONObject(response)
            state.value = obj
                .getJSONObject("current")
                .getString("temp_c")
        },
        { error ->
            Log.d("MyLog", "Error $error")
        }
    )
    queue.add(stringRequest)
}

//для нового интерфейса
private fun getData(
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
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
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list
        },
        { error ->
            Log.d("MyLog", "Error $error")
        }
    )
    queue.add(stringRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
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

