package com.example.weatherapp.service

import com.example.weatherapp.exception.ForecastProcessingException
import com.example.weatherapp.exception.WeatherApiException
import com.example.weatherapp.model.api.Period
import com.example.weatherapp.model.api.Properties
import com.example.weatherapp.model.api.WeatherApiResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class WeatherServiceTest {
    @Mock
    private lateinit var webClient: WebClient

    @Mock
    private lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: WebClient.ResponseSpec

    @Mock
    private lateinit var clientResponse: ClientResponse

    @InjectMocks
    private lateinit var weatherService: WeatherServiceImpl

    @BeforeEach
    fun setup() {
        // Set the forecastPath property using reflection
        ReflectionTestUtils.setField(weatherService, "forecastPath", "/gridpoints/MLB/33,70/forecast")
    }

    @Test
    fun `getCurrentWeather should return transformed weather data`() {
        // Given
        val mockApiResponse = createMockApiResponse()

        // Mock WebClient behavior
        whenever(webClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("/gridpoints/MLB/33,70/forecast")).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.onStatus(any(), any())).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(WeatherApiResponse::class.java)).thenReturn(Mono.just(mockApiResponse))

        // When
        val result = weatherService.getCurrentWeather()

        // Then
        StepVerifier
            .create(result)
            .expectNextMatches { response ->
                response.daily.size == 1 &&
                    response.daily[0].day_name == "Monday" &&
                    response.daily[0].temp_high_celsius == 31.1 &&
                    // (88 - 32) * 5/9 = 31.11...
                    response.daily[0].forecast_blurp == "Mostly Sunny"
            }.verifyComplete()
    }

    @Test
    fun `getCurrentWeather should handle API errors`() {
        // Given
        whenever(webClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("/gridpoints/MLB/33,70/forecast")).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)

        // Mock error response
        // Mock onStatus to allow chaining, the error will be injected in bodyToMono
        whenever(responseSpec.onStatus(any(), any())).thenReturn(responseSpec)

        whenever(responseSpec.bodyToMono(WeatherApiResponse::class.java))
            .thenReturn(Mono.error(WeatherApiException("Weather API request failed with status: 404", 404)))

        // When
        val result = weatherService.getCurrentWeather()

        // Then
        StepVerifier
            .create(result)
            .expectErrorMatches { error ->
                error is WeatherApiException &&
                    error.message == "Weather API request failed with status: 404" &&
                    error.statusCode == 404
            }.verify()
    }

    @Test
    fun `getCurrentWeather should handle missing daytime period`() {
        // Given
        val mockApiResponse = createMockApiResponseWithNoDaytimePeriod()

        // Mock WebClient behavior
        whenever(webClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri("/gridpoints/MLB/33,70/forecast")).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.onStatus(any(), any())).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(WeatherApiResponse::class.java)).thenReturn(Mono.just(mockApiResponse))

        // When
        val result = weatherService.getCurrentWeather()

        // Then
        StepVerifier
            .create(result)
            .expectErrorMatches { error ->
                error is ForecastProcessingException &&
                    error.message == "Could not find current day forecast data"
            }.verify()
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
