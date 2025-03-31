# Kotlin Springboot Reactive Weather App

## Overview

This project is a reactive REST service implemented in Kotlin Springboot that fetches weather data from the weather.gov API and transforms it according to specified requirements. The application uses Spring WebFlux, reactive programming with Mono and Flux, and follows Kotlin conventions.

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

## Technical Features

- **Reactive Programming**: Uses Spring WebFlux with Mono and Flux for non-blocking I/O
- **Layered Architecture**: Follows a clear separation of concerns
- **Testing**: Includes unit tests, controller tests, and integration tests
- **Error Handling**: Implements reactive error handling strategies
- **Configuration**: Uses application.properties for configuration
- **Logging**: Implements logging throughout the application

## Implementation Details

The implementation includes:

1. **Reactive Error Handling**: Error handling in the reactive chain
2. **Configuration Management**: Externalized API URLs and paths
3. **Logging**: Appropriate logging for operations and errors
4. **Exception Handling**: Custom exception classes and handlers
5. **Model Organization**: Separate API and response models
6. **Testing**: Tests cover both success and error paths

## Technologies Used

- Kotlin 2.1.20
- Spring Boot 3.2.8
- Spring WebFlux
- Gradle (using Wrapper 8.11)
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
java -jar build/libs/kotlin-weather-app-0.0.1-SNAPSHOT.jar
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

The application includes tests:

- Unit tests for individual components
- Controller tests using WebTestClient
- Integration tests with MockWebServer

Run the tests with:

```bash
./gradlew test
```
