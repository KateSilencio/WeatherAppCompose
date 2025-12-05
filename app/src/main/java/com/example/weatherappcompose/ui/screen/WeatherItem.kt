package com.example.weatherappcompose.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherappcompose.domain.model.WeatherModel
import com.example.weatherappcompose.ui.theme.MyBlue

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(
            list
        ) { _, item ->
            ListItem(item, currentDay)
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun ListItem(item: WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp).clickable{
                if(item.hours.isEmpty()) return@clickable
                currentDay.value = item
        },
        colors = CardDefaults.cardColors(MyBlue),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier.padding(
                    start = 5.dp,
                    top = 3.dp,
                    bottom = 3.dp
                )
            ){
                Text(
                    text = item.time,
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp),
                    )
                Text(text = item.condition,
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp),
                    )

            }
            Text(
                text = if (item.currentTemp.isNotEmpty()) {
                    item.currentTemp.toFloatOrNull()?.toInt()?.toString() ?: item.currentTemp
                } else {
                    "${item.maxTemp.toFloat().toInt()}/${item.minTemp.toFloat().toInt()}"
                },
//                text = item.currentTemp.ifEmpty {
//                    "${item.maxTemp.toFloat().toInt()}/${item.minTemp.toFloat().toInt()}"
//                },
                color = Color.White,
                style = TextStyle(fontSize = 22.sp)
            )
            AsyncImage(
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 5.dp),
                model = "https:${item.icon}",
                contentDescription = "weather_icon2"
            )
        }
    }
}

@Composable
fun DialogSearch(
    dialogState: MutableState<Boolean>,
    onSubmit: (String) -> Unit
){
    val dialogText = remember {
        mutableStateOf("")
    }

    AlertDialog(onDismissRequest = {
        dialogState.value = false
    },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = "Cancel")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Enter city name: ")
                TextField(value = dialogText.value, onValueChange = {
                    dialogText.value = it
                })
            }
        })
}