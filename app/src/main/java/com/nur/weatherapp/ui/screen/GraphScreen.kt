package com.nur.weatherapp.ui.screen

import android.util.Log
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.nur.weatherapp.data.api.WeatherApiService
import com.nur.weatherapp.data.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(
    city: String,
    // TODO: Navigation to other screens
    onNavigateBack: () -> Unit,
    onNavigateToForecast: (String) -> Unit
) {
    val context = LocalContext.current
    val apiService = remember { WeatherApiService(context) }

    var forecastList by remember { mutableStateOf<List<ForecastItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(city) {
        isLoading = true
        errorMessage = null

        apiService.getForecast(
            city = city,
            onSuccess = { forecast ->
                forecastList = forecast
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Graph for $city") },
            )
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Current weather"
                        )
                    },
                    onClick = {
                        onNavigateBack()
                    },
                    label = { Text("Current weather") }
                )
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Forecast"
                        )
                    },
                    onClick = {
                        onNavigateToForecast(city)
                    },
                    label = { Text("Forecast") }
                )
                NavigationBarItem(
                    selected = true,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Graph"
                        )
                    },
                    onClick = {},
                    label = { Text("Graph") }
                )
            }
        }
    ) {innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                forecastList.isEmpty() -> {
                    Text(
                        text = "No data for forecast",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LineChartSample(
                        forecastList = forecastList
                    )
                }
            }
        }
    }
}

@Composable
fun LineChartSample(
    forecastList: List<ForecastItem>
) {

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(calendar.time)

    val todayForecast = forecastList.filter { forecast ->
        forecast.dateTime.startsWith(today)
    }

    val displayForecast = todayForecast.ifEmpty {
        forecastList.take(8)
    }

    val temperatures = displayForecast.map {
        if (it.temperature < 0) 0.0 else it.temperature
    }

    val xAxisLabels = displayForecast.map { formatTimeForGraph(it.dateTime) }

    Log.d("GraphScreen", "Temperatures: $temperatures")
    Log.d("GraphScreen", "xAxisLabels: $xAxisLabels")

    val lineParameters: List<LineParameters> = listOf(
        LineParameters(
            label = "Temperature °C",
            data = temperatures,
            lineColor = Color(0xFF2196F3),
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        )
    )

    Box(Modifier.padding(16.dp)) {
        LineChart(
            modifier = Modifier.fillMaxSize(),
            linesParameters = lineParameters,
            isGrid = true,
            gridColor = Color.LightGray,
            xAxisData = xAxisLabels,
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
            ),
            xAxisStyle = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.W400
            ),
            oneLineChart = false,
            gridOrientation = GridOrientation.HORIZONTAL
        )
    }
}
private fun formatTimeForGraph(dateTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}
