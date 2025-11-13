package com.nur.weatherapp.data.model

data class CurrentWeather(
    val cityName: String,
    val temperature: Double,
    val description: String,
    val icon: String
)

data class ForecastItem(
    val dateTime: String,
    val temperature: Double,
    val description: String,
    val icon: String
)

data class WeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherData>
)

data class MainData(
    val temp: Double
)

data class WeatherData(
    val description: String,
    val icon: String
)

data class ForecastResponse(
    val list: List<ForecastData>,
    val city: CityData
)

data class ForecastData(
    val dt_txt: String,
    val main: MainData,
    val weather: List<WeatherData>
)

data class CityData(
    val name: String
)