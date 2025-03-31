package com.example.weatherapp.listener

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class StartupInfoLogger(
    private val environment: Environment,
) : ApplicationListener<ApplicationReadyEvent> {
    private val log = LoggerFactory.getLogger(StartupInfoLogger::class.java)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val port = environment.getProperty("local.server.port") ?: "8080" // Default to 8080 if not found
        val weatherEndpoint = "http://localhost:$port/weather"

        log.info("----------------------------------------------------------")
        log.info("Application is ready! Access the weather endpoint at:")
        log.info(" >> $weatherEndpoint")
        log.info("----------------------------------------------------------")
    }
}