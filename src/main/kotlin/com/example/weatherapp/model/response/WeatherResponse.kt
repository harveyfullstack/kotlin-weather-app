package com.example.weatherapp.model.response

/** Response model that will be returned to the client */
data class WeatherResponse(
    val daily: List<DailyForecast>,
)

data class DailyForecast(
    val day_name: String,
    val temp_high_celsius: Double,
    val forecast_blurp: String,
)
