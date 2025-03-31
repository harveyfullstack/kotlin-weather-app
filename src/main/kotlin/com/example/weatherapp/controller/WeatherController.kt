package com.example.weatherapp.controller

import com.example.weatherapp.exception.ForecastProcessingException
import com.example.weatherapp.exception.WeatherApiException
import com.example.weatherapp.model.response.WeatherResponse
import com.example.weatherapp.service.WeatherService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/weather")
class WeatherController(
    private val weatherService: WeatherService,
) {
    private val log = LoggerFactory.getLogger(WeatherController::class.java)

    @GetMapping
    fun getWeather(): Mono<WeatherResponse> {
        log.debug("Received request for weather forecast")
        return weatherService
            .getCurrentWeather()
            .doOnSuccess { response ->
                log.debug("Successfully returned weather forecast for {}", response.daily.firstOrNull()?.day_name ?: "unknown day")
            }
    }

    @ExceptionHandler(WeatherApiException::class)
    fun handleWeatherApiException(ex: WeatherApiException): ResponseEntity<Map<String, String>> {
        log.error("Weather API exception: {}", ex.message)
        val status = ex.statusCode?.let { HttpStatus.valueOf(it) } ?: HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity
            .status(status)
            .body(mapOf("error" to (ex.message ?: "Weather API error")))
    }

    @ExceptionHandler(ForecastProcessingException::class)
    fun handleForecastProcessingException(ex: ForecastProcessingException): ResponseEntity<Map<String, String>> {
        log.error("Forecast processing exception: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to (ex.message ?: "Error processing forecast data")))
    }
}
