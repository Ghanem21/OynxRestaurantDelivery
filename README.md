# Onyx Restaurant Delivery

A modern Android app for restaurant delivery management, built with Jetpack Compose and following
Clean Architecture principles.

## Features

- Driver login authentication
- Multi-language support (English and Arabic)
- Delivery order management
- Session management and expiration handling

## Architecture

This project follows Clean Architecture principles and is structured into three main modules:

1. **app** - Presentation layer (UI, ViewModels, Navigation)
2. **domain** - Business logic layer (Use Cases, Models, Repository interfaces)
3. **data** - Data layer (API, Database, Repository implementations)

For more details about the architecture, see [README_ARCHITECTURE.md](./README_ARCHITECTURE.md).

## Design Patterns

The application implements several design patterns to ensure maintainable, testable, and scalable
code:

- **MVVM (Model-View-ViewModel)**: Separates UI from business logic and facilitates testing
- **Repository Pattern**: Abstracts data sources and provides a clean API to the domain layer
- **Factory Pattern**: Used for creating repository instances and other components
- **Observer Pattern**: Implemented via Kotlin Flow for reactive data streams
- **Dependency Injection**: Using Hilt to manage dependencies across the application
- **Use Case Pattern**: Encapsulates business logic in single-responsibility classes
- **Builder Pattern**: Used for constructing complex objects
- **Adapter Pattern**: Used for converting between data models in different layers

## Technology Stack

- **UI**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit
- **Local Storage**: Room, SharedPreferences
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Compose

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Build and run the app

## Project Structure

The app is organized by features, with each feature having its own vertical slice of the
architecture:

```
app/src/main/java/com/androidghanem/oynxrestaurantdelivery/
├── common/                  # Common utilities shared across features
├── features/                # Feature-specific code organized by feature
│   ├── login/               # Login feature
│   └── home/                # Home feature
└── ui/                      # Shared UI components and resources
```