package com.example.weatherapp.util

import kotlin.math.roundToInt

/**
 * Utility class for temperature conversion
 */
object TemperatureConverter {
    /**
     * Converts temperature from Fahrenheit to Celsius
     *
     * @param fahrenheit Temperature in Fahrenheit
     * @return Temperature in Celsius, rounded to 1 decimal place
     */
    fun fahrenheitToCelsius(fahrenheit: Int): Double {
        val celsius = (fahrenheit - 32) * 5.0 / 9.0
        return (celsius * 10).roundToInt() / 10.0 // Round to 1 decimal place using roundToInt
    }
}
