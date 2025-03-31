package com.example.weatherapp

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
class WeatherAppApplicationTests {
    @MockBean
    private lateinit var webClient: WebClient

    @Test
    fun contextLoads() {
        // Verify that the application context loads successfully
    }
}