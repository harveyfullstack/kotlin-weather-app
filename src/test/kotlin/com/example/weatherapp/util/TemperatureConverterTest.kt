package com.example.weatherapp.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TemperatureConverterTest {
    @Test
    fun `fahrenheitToCelsius should correctly convert temperature`() {
        // Test cases with expected results rounded to 1 decimal place
        val testCases =
            mapOf(
                32 to 0.0, // Freezing point
                212 to 100.0, // Boiling point
                88 to 31.1, // Value from our example
                68 to 20.0, // Room temperature
                -40 to -40.0, // Same in both scales
            )

        testCases.forEach { (fahrenheit, expectedCelsius) ->
            val actualCelsius = TemperatureConverter.fahrenheitToCelsius(fahrenheit)
            assertEquals(
                expectedCelsius,
                actualCelsius,
                "Converting $fahrenheit°F should result in $expectedCelsius°C",
            )
        }
    }
}
