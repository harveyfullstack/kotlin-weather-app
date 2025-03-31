package com.example.weatherapp

import com.example.weatherapp.model.api.Period
import com.example.weatherapp.model.api.Properties
import com.example.weatherapp.model.api.WeatherApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WeatherIntegrationTest {
    companion object {
        private val mockWebServer = MockWebServer()

        @JvmStatic
        @BeforeAll
        fun startServer() {
            mockWebServer.start()
        }

        @JvmStatic
        @AfterAll
        fun stopServer() {
            mockWebServer.shutdown()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("weather.api.base-url") { mockWebServer.url("/").toString() } // Use url() for full path
            registry.add("weather.api.forecast-path") { "/gridpoints/MLB/33,70/forecast" } // Keep relative path
        }
    }

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `integration test for weather endpoint - success case`() {
        // Given
        val mockApiResponse = createMockApiResponse()
        val mockResponseJson = objectMapper.writeValueAsString(mockApiResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponseJson),
        )

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
    fun `integration test for weather endpoint - API error case`() {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"error\": \"Not Found\"}"),
        )

        // When/Then
        webTestClient
            .get()
            .uri("/weather")
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_FOUND)
            .expectBody()
            .jsonPath("$.error")
            .exists()
    }

    @Test
    fun `integration test for weather endpoint - no daytime period case`() {
        // Given
        val mockApiResponse = createMockApiResponseWithNoDaytimePeriod()
        val mockResponseJson = objectMapper.writeValueAsString(mockApiResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponseJson),
        )

        // When/Then
        webTestClient
            .get()
            .uri("/weather")
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectBody()
            .jsonPath("$.error")
            .exists()
    }

    private fun createMockApiResponse(): WeatherApiResponse {
        val period =
            Period(
                number = 1,
                name = "This Afternoon",
                startTime = "2025-03-31T12:00:00-04:00",
                endTime = "2025-03-31T18:00:00-04:00",
                isDaytime = true,
                temperature = 88,
                temperatureUnit = "F",
                shortForecast = "Mostly Sunny",
            )

        val properties = Properties(periods = listOf(period))
        return WeatherApiResponse(properties = properties)
    }

    private fun createMockApiResponseWithNoDaytimePeriod(): WeatherApiResponse {
        val period =
            Period(
                number = 1,
                name = "Tonight",
                startTime = "2025-03-31T18:00:00-04:00",
                endTime = "2025-04-01T06:00:00-04:00",
                isDaytime = false,
                temperature = 67,
                temperatureUnit = "F",
                shortForecast = "Partly Cloudy",
            )

        val properties = Properties(periods = listOf(period))
        return WeatherApiResponse(properties = properties)
    }
}