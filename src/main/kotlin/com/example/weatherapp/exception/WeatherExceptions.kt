package com.example.weatherapp.exception

/**
 * Base exception class for weather forecast processing errors
 */
open class WeatherProcessingException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Exception thrown when there is an issue processing the forecast data
 */
class ForecastProcessingException(
    message: String,
    cause: Throwable? = null,
) : WeatherProcessingException(message, cause)

/**
 * Exception thrown when there is an issue with the weather API
 */
class WeatherApiException(
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null,
) : WeatherProcessingException(message, cause)
