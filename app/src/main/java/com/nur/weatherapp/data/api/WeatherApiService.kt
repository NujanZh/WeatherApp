package com.nur.weatherapp.data.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nur.weatherapp.data.model.CurrentWeather
import com.nur.weatherapp.data.model.ForecastItem
import org.json.JSONObject

class WeatherApiService(private val context: Context) {

    private val apiKey = "94c0a6f782b81362727bba11a2534666"
    private val baseUrl = "https://api.openweathermap.org/data/2.5"

    private val requestQueue = Volley.newRequestQueue(context)

    fun getCurrentWeather(
        city: String,
        onSuccess: (CurrentWeather) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/weather?q=$city&appid=$apiKey&units=metric&lang=en"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val weather = parseCurrentWeather(response)
                    onSuccess(weather)
                } catch (e: Exception) {
                    onError("Error processing data: ${e.message}")
                }
            },
            { error ->
                val errorMessage = when (error.networkResponse?.statusCode) {
                    404 -> "City not found"
                    401 -> "Wrong API key"
                    else -> "Network error: ${error.message}"
                }
                onError(errorMessage)
            }
        )

        requestQueue.add(request)
    }

    fun getForecast(
        city: String,
        onSuccess: (List<ForecastItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "$baseUrl/forecast?q=$city&appid=$apiKey&units=metric&lang=en"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val forecast = parseForecast(response)
                    onSuccess(forecast)
                } catch (e: Exception) {
                    onError("Error processing data: ${e.message}")
                }
            },
            { error ->
                val errorMessage = when (error.networkResponse?.statusCode) {
                    404 -> "City not found"
                    401 -> "Wrong API key"
                    else -> "Network error: ${error.message}"
                }
                onError(errorMessage)
            }
        )

        requestQueue.add(request)
    }

    private fun parseCurrentWeather(json: JSONObject): CurrentWeather {
        val main = json.getJSONObject("main")
        val weather = json.getJSONArray("weather").getJSONObject(0)

        return CurrentWeather(
            cityName = json.getString("name"),
            temperature = main.getDouble("temp"),
            description = weather.getString("description"),
            icon = weather.getString("icon")
        )
    }

    private fun parseForecast(json: JSONObject): List<ForecastItem> {
        val list = json.getJSONArray("list")
        val forecastList = mutableListOf<ForecastItem>()

        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            val main = item.getJSONObject("main")
            val weather = item.getJSONArray("weather").getJSONObject(0)

            forecastList.add(
                ForecastItem(
                    dateTime = item.getString("dt_txt"),
                    temperature = main.getDouble("temp"),
                    description = weather.getString("description"),
                    icon = weather.getString("icon")
                )
            )
        }

        return forecastList
    }
}