package com.nur.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nur.weatherapp.ui.screen.CurrentWeatherScreen
import com.nur.weatherapp.ui.screen.ForecastScreen
import com.nur.weatherapp.ui.screen.GraphScreen
import com.nur.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "current_weather"
                ) {
                    composable("current_weather") {
                        CurrentWeatherScreen(
                            onNavigateToForecast = { city ->
                                navController.navigate("forecast/$city")
                            },
                            onNavigateToGraph = { city ->
                                navController.navigate("graph/$city")
                            }
                        )
                    }
                    composable("forecast/{city}") { backStackEntry ->
                        val city = backStackEntry.arguments?.getString("city") ?: ""
                        ForecastScreen(
                            city = city,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToGraph = { city ->
                                navController.navigate("graph/$city") {
                                    popUpTo("forecast/$city") { inclusive = false }
                                }
                            }
                        )
                    }
                    composable("graph/{city}") { backStackEntry ->
                        val city = backStackEntry.arguments?.getString("city") ?: ""
                        GraphScreen(
                            city = city,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToForecast = { city ->
                                navController.navigate("forecast/$city") {
                                    popUpTo("graph/$city") { inclusive = false }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}