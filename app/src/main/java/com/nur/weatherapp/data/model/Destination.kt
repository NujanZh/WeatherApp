package com.nur.weatherapp.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
}
