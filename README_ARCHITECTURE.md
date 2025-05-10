# Onyx Restaurant Delivery App Architecture

This document provides an overview of the architecture and organization of the Onyx Restaurant
Delivery app.

## Clean Architecture

The app follows a Clean Architecture approach with a modularized structure:

### Modules

1. **app** - Presentation layer and app-specific logic
2. **domain** - Business rules and models
3. **data** - Data sources, repositories, and networking

### Clean Architecture Layers

- **Presentation Layer** (app module)
    - UI components
    - ViewModels
    - Navigation

- **Domain Layer** (domain module)
    - Use Cases
    - Domain Models
    - Repository Interfaces

- **Data Layer** (data module)
    - API Services
    - Local Storage
    - Repository Implementations
    - Data Models

## Package Structure

### App Module Structure

```
app/src/main/java/com/androidghanem/oynxrestaurantdelivery/
├── common/                  # Common utilities shared across features
│   ├── di/                  # App-level dependency injection
│   └── util/                # Common utilities
├── features/                # Feature-specific code organized by feature
│   ├── login/               # Login feature
│   │   ├── di/              # Login-specific DI
│   │   ├── domain/          # Login-specific use cases
│   │   └── presentation/    # Login UI and ViewModels
│   │       └── components/  # Login-specific UI components
│   └── home/                # Home feature (similar structure)
├── ui/                      # Shared UI components and resources
│   ├── components/          # Shared UI components
│   ├── navigation/          # Navigation configuration
│   ├── theme/               # Theme definitions
│   └── util/                # UI utilities
└── MainActivity.kt          # Main entry point
```

### Domain Module Structure

```
domain/src/main/java/com/androidghanem/domain/
├── constants/               # Domain-level constants
├── model/                   # Business models
├── repository/              # Repository interfaces
└── utils/                   # Domain utilities
```

### Data Module Structure

```
data/src/main/java/com/androidghanem/data/
├── di/                      # Data layer dependency injection
├── local/                   # Local storage (database, preferences)
│   └── db/                  # Room database
├── network/                 # Network-related code
│   ├── api/                 # API service interfaces
│   └── model/               # Network data models
├── preferences/             # SharedPreferences management
├── repository/              # Repository implementations
└── session/                 # Session management
```

## Feature-Based Organization

The app is organized primarily by features (Login, Home, etc.), with each feature having its own
complete vertical slice:

- **Presentation Layer**: UI components and ViewModels
- **Domain Layer**: Use cases (feature-specific business logic)
- **Data Layer**: Repository implementations and data sources

This organization makes it easy to understand which files are related to a specific feature and
keeps related code together.

## Dependency Injection

The app uses Hilt for dependency injection with the following modules:

- **AppModule**: App-level dependencies
- **DataModule**: Data layer dependencies
- **Feature-specific modules**: Dependencies required for specific features

## Design Patterns

- **MVVM**: For UI architecture
- **Repository**: For data operations
- **Use Cases**: For business logic
- **Observer**: For reactive data flow using Kotlin Flow