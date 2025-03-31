package com.example.weatherapp.config

import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig {
    @Value("\${weather.api.base-url}")
    private lateinit var baseUrl: String

    @Bean
    fun webClient(): WebClient {
        val httpClient =
            HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 5 seconds connect timeout
                .responseTimeout(Duration.ofSeconds(10)) // 10 seconds response timeout

        return WebClient
            .builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(ReactorClientHttpConnector(httpClient)) // Apply configured HttpClient
            .build()
    }
}
