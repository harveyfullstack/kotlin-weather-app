package com.example.weatherapp.controller

import com.example.weatherapp.exception.ForecastProcessingException
import com.example.weatherapp.exception.WeatherApiException
import com.example.weatherapp.model.response.DailyForecast
import com.example.weatherapp.model.response.WeatherResponse
import com.example.weatherapp.service.WeatherService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(WeatherController::class)
class WeatherControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var weatherService: WeatherService

    @Test
    fun `getWeather should return weather data`() {
        // Given
        val mockResponse =
            WeatherResponse(
                daily =
                    listOf(
                        DailyForecast(
                            day_name = "Monday",
                            temp_high_celsius = 31.1,
                            forecast_blurp = "Mostly Sunny",
                        ),
                    ),
            )

        whenever(weatherService.getCurrentWeather()).thenReturn(Mono.just(mockResponse))

        // When/Then
        webTestClient
            .get()
            .uri("/weather")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.daily[0].day_name")
            .isEqualTo("Monday")
            .jsonPath("$.daily[0].temp_high_celsius")
            .isEqualTo(31.1)
            .jsonPath("$.daily[0].forecast_blurp")
            .isEqualTo("Mostly Sunny")
    }

    @Test
    fun `getWeather should return 500 when service throws ForecastProcessingException`() {
        // Given
        val errorMessage = "Could not find current day forecast data"
        whenever(weatherService.getCurrentWeather())
            .thenReturn(Mono.error(ForecastProcessingException(errorMessage)))

        // When/Then
        webTestClient
            .get()
            .uri("/weather")
            .exchange()
            .expectStatus()
            .isEqualTo(500)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(errorMessage)
    }

    @Test
    fun `getWeather should return appropriate status code when service throws WeatherApiException`() {
        // Given
        val errorMessage = "Weather API request failed with status: 404"
        val statusCode = 404
        whenever(weatherService.getCurrentWeather())
            .thenReturn(Mono.error(WeatherApiException(errorMessage, statusCode)))

        // When/Then
        webTestClient
            .get()
            .uri("/weather")
            .exchange()
            .expectStatus()
            .isEqualTo(404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(errorMessage)
    }
}
