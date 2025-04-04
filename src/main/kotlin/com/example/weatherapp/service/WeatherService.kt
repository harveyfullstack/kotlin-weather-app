package com.example.weatherapp.service

import com.example.weatherapp.exception.ForecastProcessingException
import com.example.weatherapp.exception.WeatherApiException
import com.example.weatherapp.model.api.WeatherApiResponse
import com.example.weatherapp.model.response.DailyForecast
import com.example.weatherapp.model.response.WeatherResponse
import com.example.weatherapp.util.TemperatureConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

interface WeatherService {
    fun getCurrentWeather(): Mono<WeatherResponse>
}

@Service
class WeatherServiceImpl(
    private val webClient: WebClient,
) : WeatherService {
    private val log = LoggerFactory.getLogger(WeatherServiceImpl::class.java)

    @Value("\${weather.api.forecast-path}")
    private lateinit var forecastPath: String

    override fun getCurrentWeather(): Mono<WeatherResponse> {
        log.info("Fetching weather forecast from path: {}", forecastPath)

        return webClient
            .get()
            .uri(forecastPath)
            .retrieve()
            // Handle HTTP status errors
            .onStatus({ status -> status.isError }) { clientResponse ->
                log.error("Weather API request failed with status: {}", clientResponse.statusCode())
                Mono.error(
                    WeatherApiException(
                        "Weather API request failed with status: ${clientResponse.statusCode()}",
                        clientResponse.statusCode().value(),
                    ),
                )
            }.bodyToMono(WeatherApiResponse::class.java)
            .doOnNext { response ->
                log.debug("Received weather API response with {} periods", response.properties.periods.size)
            }.map { apiResponse -> transformResponse(apiResponse) }
            .doOnError { error ->
                log.error("Error processing weather forecast: {}", error.message, error)
            }.onErrorMap { error ->
                when {
                    error is IllegalStateException && error.message?.contains("No daytime period found") == true -> {
                        ForecastProcessingException("Could not find current day forecast data", error)
                    }
                    error !is WeatherApiException -> {
                        ForecastProcessingException("Error processing weather data: ${error.message}", error)
                    }
                    else -> error
                }
            }
    }

    private fun transformResponse(apiResponse: WeatherApiResponse): WeatherResponse {
        // Try to find the current day's daytime period
        val today = java.time.LocalDate.now()
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        var currentDayPeriod =
            apiResponse.properties.periods
                .firstOrNull {
                    it.isDaytime && java.time.LocalDate.parse(it.startTime, formatter) == today
                }

        // If today's daytime period isn't found, fall back to the first available daytime period
        if (currentDayPeriod == null) {
            log.warn("Today's daytime forecast not found in API response. Falling back to the first available daytime period.")
            currentDayPeriod =
                apiResponse.properties.periods
                    .firstOrNull { it.isDaytime }
                    ?: throw IllegalStateException("No daytime period found in the forecast at all.")
        }

        log.info("Transforming weather data for period: {}", currentDayPeriod.name)

        // Extract day name from the period
        // Use the start time of the found period to extract the day name reliably
        val dayName = extractDayName(currentDayPeriod.startTime)

        // Convert temperature from Fahrenheit to Celsius
        val tempHighCelsius = TemperatureConverter.fahrenheitToCelsius(currentDayPeriod.temperature)

        // Create the daily forecast
        val dailyForecast =
            DailyForecast(
                day_name = dayName,
                temp_high_celsius = tempHighCelsius,
                forecast_blurp = currentDayPeriod.shortForecast,
            )

        log.debug(
            "Transformed forecast: day={}, temp={}°C, forecast={}",
            dayName,
            tempHighCelsius,
            currentDayPeriod.shortForecast,
        )

        // Return the weather response
        return WeatherResponse(daily = listOf(dailyForecast))
    }

    private fun extractDayName(startTime: String): String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = LocalDateTime.parse(startTime, formatter)
        return dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    }
}
