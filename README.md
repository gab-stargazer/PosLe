# PosLe (Point of Sale)

[![Android Debug Build](https://github.com/gab-stargazer/PosLe/actions/workflows/android-debug-build.yml/badge.svg)](https://github.com/gab-stargazer/PosLe/actions/workflows/android-debug-build.yml)

## Architecture
For a detailed overview of the project structure and design patterns, see the [Architecture Documentation](./ARCHITECTURE.md). For a developer-oriented walkthrough of the codebase — modules, layers, key classes, data flow, and key flows — see the [Codebase Guide](./CODEBASE.md). For app screenshots, see the [Screenshot Gallery](./SCREENSHOTS.md) or the [PosLe landing page](./docs/index.html).

PosLe is a modern, cross-platform Point of Sale system built with Kotlin Multiplatform. It provides a robust solution for managing transactions, products, inventory, and sales recaps across Android and Desktop platforms.

## Key Features

- **Transaction Management:** Efficiently process sales with support for individual products and bundled items.
- **Product & Inventory:** Manage a comprehensive product catalog, including variants, categories, and stock tracking with low-stock alerts.
- **Bundles:** Create and manage product bundles for promotional offers or grouped sales.
- **Sales Recaps:** Generate and view detailed transaction histories and sales summaries.
- **Export Notifications:** Get a platform notification when a product-list export finishes (see [Export Notifications](./ARCHITECTURE.md#feature-export-notifications)).
- **QR Scanning:** Quickly find products or process transactions using the built-in QR scanner.
- **Data Persistence:** Offline-first approach using Room database for reliable data management.
- **Cross-Platform:** Shared business logic and UI using Compose Multiplatform.

## Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **Architecture:** Clean Architecture with [Decompose](https://arkivanov.github.io/Decompose/) for navigation and lifecycle management.
- **Dependency Injection:** [Koin](https://insert-koin.io/)
- **Database:** [Room](https://developer.android.com/training/data-storage/room) (KMP version)
- **Local Storage:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for settings.
- **Paging:** [Jetpack Paging](https://developer.android.com/topic/libraries/architecture/paging/v3-network-db) for efficient list rendering.
- **Concurrency:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & Flow.

## Project Structure

- `androidApp`: Android-specific entry point and resources.
- `desktopApp`: JVM/Desktop-specific entry point and resources.
- `shared`: The core of the application, containing:
    - `commonMain`: Shared UI (Compose), business logic (Domain), and data handling (Data).
    - `androidMain` & `jvmMain`: Platform-specific implementations for storage, platform info, etc.

## Getting Started

### Prerequisites

- Android Studio or IntelliJ IDEA
- JDK 11 or higher
- Android SDK

### Running the Apps

- **Android:** `./gradlew :androidApp:assembleDebug`
- **Desktop:**
    - Hot reload: `./gradlew :desktopApp:hotRun --auto`
    - Standard run: `./gradlew :desktopApp:run`

### Running Tests

- **Android Host Tests:** `./gradlew :shared:testAndroidHostTest`
- **Desktop Tests:** `./gradlew :shared:jvmTest`

## License

PosLe is licensed under the [Apache License 2.0](LICENSE). See the LICENSE file for details.
