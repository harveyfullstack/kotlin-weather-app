# Kotlin Springboot Reactive Weather App

## Overview

This project is a reactive REST service implemented in Kotlin Springboot that fetches weather data from the weather.gov API and transforms it according to specified requirements. The application demonstrates the use of Spring WebFlux, reactive programming with Mono and Flux, and Kotlin best practices.

## Requirements

The application fulfills the following requirements:

1. Build a simple REST request implemented in Kotlin Springboot reactive
2. Call the endpoint: https://api.weather.gov/gridpoints/MLB/33,70/forecast
3. Return the following result structure for the current day:
   ```json
   {
     "daily": [{
       "day_name": "Monday",
       "temp_high_celsius": 27.2, 
       "forecast_blurp": "Partly Sunny"
     }]
   }
   ```
4. All transformations utilize the reactive stack (Mono, Flux)

## Project Structure

```
com.example.weatherapp
├── WeatherAppApplication.kt (Main application class)
├── config
│   └── WebClientConfig.kt (WebClient configuration)
├── controller
│   └── WeatherController.kt (REST endpoint)
├── exception
│   └── WeatherExceptions.kt (Custom exception classes)
├── model
│   ├── api
│   │   └── WeatherApiResponse.kt (API response models)
│   └── response
│       └── WeatherResponse.kt (Application response models)
├── service
│   └── WeatherService.kt (Service layer)
└── util
    └── TemperatureConverter.kt (Utility for temperature conversion)
```

## Key Features

- **Reactive Programming**: Uses Spring WebFlux with Mono and Flux for non-blocking I/O
- **Clean Architecture**: Follows a layered architecture with clear separation of concerns
- **Comprehensive Testing**: Includes unit tests, controller tests, and integration tests
- **Robust Error Handling**: Implements comprehensive reactive error handling
- **Externalized Configuration**: Uses application.properties for configuration
- **Comprehensive Logging**: Implements proper logging throughout the application

## Implementation Highlights

The implementation includes several key features that demonstrate senior-level engineering practices:

1. **Explicit Reactive Error Handling**: Comprehensive error handling in the reactive chain
2. **Configuration Management**: Externalized API URLs and paths to application.properties
3. **Logging**: Proper logging throughout the application
4. **Enhanced Exception Handling**: Custom exception classes and controller exception handlers
5. **Clean Model Organization**: Separated API and response models
6. **Thorough Testing**: Testing of both success and error paths for all components

## Technologies Used

- Kotlin 1.9.22
- Spring Boot 3.2.3
- Spring WebFlux
- Gradle
- JUnit 5
- MockWebServer for testing

## Building and Running

### Prerequisites

- JDK 17 or higher
- No need to install Gradle - the project includes Gradle wrapper scripts

### Build

```bash
# On Linux/macOS
./gradlew build

# On Windows
gradlew.bat build
```

### Run

```bash
# On Linux/macOS
./gradlew bootRun

# On Windows
gradlew.bat bootRun
```

### Run via JAR

Alternatively, after building the project, you can run the generated JAR file directly:

```bash
java -jar build/libs/weatherapp-0.0.1-SNAPSHOT.jar
```

*Note: The JAR is located in the build/libs directory after compilation.*

If you encounter permission issues with the Gradle wrapper script on Linux/macOS, you may need to make it executable:

```bash
chmod +x ./gradlew
```

The application will start on port 8080 by default. You can access the weather endpoint at:

```
http://localhost:8080/weather
```

## Testing

The application includes comprehensive tests:

- Unit tests for individual components
- Controller tests using WebTestClient (including error paths)
- Integration tests with MockWebServer (including error scenarios)

Run the tests with:

```bash
./gradlew test