package com.example.weatherapp.model.api

/** API response models for deserializing the weather.gov API response */
data class WeatherApiResponse(
    val properties: Properties,
)

data class Properties(
    val periods: List<Period>,
)

data class Period(
    val number: Int,
    val name: String,
    val startTime: String,
    val endTime: String,
    val isDaytime: Boolean,
    val temperature: Int,
    val temperatureUnit: String,
    val shortForecast: String,
)
