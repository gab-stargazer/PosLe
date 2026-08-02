# PosLe Codebase Guide

This document is a developer-oriented walkthrough of the **PosLe** codebase —
the modules, layers, key classes, data flow, and the most important flows
(transactions, product management, recaps, exports, settings).

> **New here?** Start with [README.md](./README.md) for the project overview and
> [ARCHITECTURE.md](./ARCHITECTURE.md) for the high-level architecture diagrams.

---

## Table of Contents

1. [Module Overview](#module-overview)
2. [Layer Map (package by package)](#layer-map)
3. [The App Shell: entry points & DI](#the-app-shell)
4. [Navigation & Decompose](#navigation--decompose)
5. [The Data Layer (Room)](#the-data-layer-room)
6. [Domain Layer: Components & State](#domain-layer-components--state)
7. [The UI Layer](#the-ui-layer)
8. [Platform Bridges (expect/actual)](#platform-bridges-expectactual)
9. [Settings & DataStore](#settings--datastore)
10. [Key Flows](#key-flows)
11. [Export & Notification Flows](#export--notification-flows)
12. [Utilities](#utilities)
13. [Testing](#testing)
14. [Conventions & Gotchas](#conventions--gotchas)

---

## Module Overview

```
PosLe/
├── androidApp/          # Android entry point, manifest, DI, WorkManager
├── desktopApp/          # JVM/desktop entry point (Compose Desktop window)
└── shared/              # The core: shared UI + domain + data (KMP)
    ├── src/commonMain/  # Shared Kotlin (UI, domain, data, util)
    ├── src/androidMain/ # Android-only implementations (expect/actual)
    ├── src/jvmMain/     # Desktop/JVM-only implementations
    ├── src/jvmTest/     # JVM unit tests (run with :shared:jvmTest)
    └── src/androidHostTest/ # Android host tests
```

| Module | Purpose |
|---|---|
| `shared` | Everything cross-platform: Compose UI, Decompose components, Room (KMP), DataStore, Paging, export/import, printing |
| `androidApp` | Android launcher (`MainActivity`), `RootContent` glue, WorkManager (`PdfExportWorker`), `NotificationHelper` usage, Firebase `google-services` (per build type) |
| `desktopApp` | Compose Desktop window; phone-gated so the mobile UI is intentionally hidden on desktop; `savePdfAndNotify` |

**Key stats:** ~168 files in `commonMain`, 53 screens, 6 Room entities + 6 DAOs,
6 repositories, 9 Decompose components, ~12 expect/actual bridges.

---

## Layer Map

### `shared/src/commonMain/kotlin/org/lelestacia/posle/`

| Package | Contents | Role |
|---|---|---|
| `App.kt` | `App(content)` | Root composable wrapper (`MaterialExpressiveTheme`) |
| `data/` | `PosLeDB`, `SettingManager`, entities, DAOs, repository impls, converters, `TransactionRunner` | Room database + DataStore + repository implementations |
| `di/` | `SharedModule` (Koin) | DI wiring for DAOs, repositories, `SettingManager`, `TransactionRunner`, `SnackbarHostState` |
| `domain/` | Models, repository interfaces, **components** (state machines), state/event classes | Business logic, clean-architecture domain layer |
| `navigation/` | `Config`, `NavConfig`, `NavDestination`, `PosLeComponent` | Decompose navigation: root stack (full-screen) + tab stack (drawer) |
| `ui/` | `component/`, `screen/` (53 screens), `platform/`, `theme/` | Compose Multiplatform UI |
| `util/` | `AppLogger`, `FileStorage`, `ExcelManager`, `RupiahVisualTransformation`, `TextParse`, `TimeUtil`, `FormatDate`, `Util`, `Serializer`, `Value`, `Aliases`, etc. | Cross-platform helpers |

### `shared/src/androidMain/` (Android-only implementations)

`FileStorage.android.kt`, `AppLogger.android.kt`, `ImageBitmapExt.android.kt`,
`Printer.android.kt`, `CreateDatastore.kt`, `NotificationHelper.kt`, and the
`ui/platform/*.android.kt` actuals (camera permission, back handler, QR
scanner, image picker, app version, export notifier).

### `shared/src/jvmMain/` (Desktop-only implementations)

`FileStorage.jvm.kt`, `AppLogger.jvm.kt`, `ImageBitmapExt.jvm.kt`,
`DesktopNotifier.kt`, and the `ui/platform/*.jvm.kt` actuals.

---

## The App Shell

### Android (`androidApp`)

- **`MainActivity.kt`** — the single Android entry:
  - `onCreate` sets the app theme (light scheme with `onSurfaceLightHighContrast`
    scrim) and `setContent { ... }`.
  - Declares a **notification permission** state (`POST_NOTIFICATIONS`) with a
    rationale dialog; if granted (or API ≤ 32) it can enqueue the PDF export.
  - Creates `PosLeComponent` (the root Decompose component) with
    `onPrintRecap = { enqueuePdfExport(...) }` — the recap "print" goes through
    a WorkManager worker.
- **`RootContent.kt`** — glue that renders the `DashboardScreen` for the root
  child and wires platform UI (bluetooth permission, etc.).
- **`di/PosLeApplication.kt`** — Koin `startKoin` with `sharedModule` +
  `androidModule`; provides the Room DB builder (`DbBuilder.kt`, with
  `MIGRATION_2_3`, `MIGRATION_3_4`).
- **`worker/PdfExportWorker.kt`** — WorkManager `CoroutineWorker` that generates
  a recap PDF (`TransactionReportGenerator.generateAsBytes`) and saves it via
  `FileStorage.saveToPublicDocuments`, then calls
  `NotificationHelper.notifySuccess(...)` (title "Laporan Selesai").
- **`worker/AndroidRunnableService.kt`** — implements `RunnableService`
  (`enqueue(id, serializedData)`) using `WorkManager.enqueueUniqueWork`.

### Desktop (`desktopApp`)

- **`main.kt`** — `application { Window(1280×800) { App { PhoneOnlyContent { } } } }`.
  The desktop window is **not** phone-sized, so `PhoneOnlyContent` renders
  nothing — the mobile UI is intentionally absent on desktop. Also defines
  `savePdfAndNotify(...)` (used by the recap flow) which saves the PDF and
  calls `DesktopNotifier.notifyPdfGenerated(...)`.

### DI (`shared/di/SharedModule.kt`)

Koin module registering:

- All 6 DAOs (`ProductDao`, `StockDao`, `TransactionDao`, `VariantDao`,
  `CategoryDao`, `BundleDao`) — each `single { get<PosLeDB>().xxxDao() }`
- `SnackbarHostState` (shared across screens for snackbars)
- `SettingManager` (DataStore-backed settings)
- `TransactionRunner` → `TransactionRunnerImpl` (db transaction helper)
- 6 repository impls bound to their interfaces (`ProductRepository`,
  `StockRepository`, `TransactionRepository`, `VariantRepository`,
  `CategoryRepository`, `BundleRepository`)

---

## Navigation & Decompose

Navigation uses **Decompose** (v3.5.0) with two stacks:

```
PosLeComponent
├── rootNavigation: StackNavigation<Config>     # full-screen pages
│   └── children: ChildStack<Config, Child>
│       ├── Dashboard (tab container)
│       ├── TransactionProductConfig
│       ├── TransactionSearch
│       ├── TransactionView
│       ├── ProductAddEdit
│       ├── BundleAddEdit
│       ├── QrScanner
│       ├── VariantView
│       └── TransactionRecapProductView
└── tabNavigation: StackNavigation<NavConfig>   # drawer tabs
    └── tabChildren: ChildStack<NavConfig, NavChild>
        ├── TransactionAdd
        ├── TransactionRecap
        ├── ProductList
        ├── ProductInboundOutbound
        └── Setting
```

- **`Config`** (sealed interface) — serializable full-screen destinations.
  `Child` is the component wrapper.
- **`NavConfig`** (sealed interface) — drawer-tab destinations. `NavChild` is
  the wrapper. `NavDestination` enum maps each tab to its icon + title.
- **`PosLeComponent`** — creates all children via `createChild(config)` /
  `createTabChild(config)`, wires navigation callbacks (`onNavigate`,
  `onPrintRecap`, `onNavigateToProductConfig`, `onNavigateToQRScanner`,
  `onVariantsSelected`), and handles the dashboard's drawer navigation
  (`handleDashboardNavigation`).
- **`DashboardScreen`** hosts the `ModalNavigationDrawer` with the tab stack;
  drawer items = `NavDestination.entries`, with `BurgundyRed` selected color.
- **`RootContent`** (androidApp) hosts the top-level `Children(...)` stack with
  a fade animation between root destinations.

---

## The Data Layer (Room)

### Database

- **`PosLeDB`** (`data/PosLeDB.kt`) — `@Database(version = 2)` Room DB with 6
  entities, `@Dao`s, converters, and `MIGRATION_2_3` / `MIGRATION_3_4`.
  Declares `expect object AppDatabaseConstructor` (per-platform builder).
- **`TransactionRunner`** — wraps `db.withTransaction { }` for atomic writes.

### Entities (`data/entity/`)

| Entity | Table | Notes |
|---|---|---|
| `ProductEntity` | `product` | id, name, unit, sku_number, image_uri, created/updated |
| `ProductSellPriceEntity` | `product_sell_price` | price history per product |
| `ProductBuyPriceEntity` | `product_buy_price` | buy price history |
| `ProductWithVariantsAndStock` | — | join/aggregate for product + variants + stock |
| `StockEntity` | `stock` | product_id (PK), stock, updated_at |
| `StockMovementEntity` | `stock_movement` | movement history (IN/OUT, amount, note) |
| `TransactionEntity` | `transaction` | customer_name, is_recapped, timestamps |
| `TransactionItemEntity` | `transaction_item` | transaction_id, item type |
| `TransactionItemProductEntity` | `transaction_item_product` | products in each item |
| `TransactionWithItems` | — | aggregate |
| `VariantEntity` | `variant` | name, price adjustment |
| `CategoryEntity` | `category` | name |
| `BundleEntity` / `BundleProductEntity` | `bundle` / `bundle_product` | bundle + junction |

### DAOs (`data/dao/`)

Each DAO exposes `@Query`/`@Insert`/`@Update`/`@Delete` returning
`PagingSource<Int, ...>` (Paging 3) or `Flow`. Examples:

- `ProductDao.readProductsByName(name)` → `PagingSource` (LIKE search, ASC)
- `ProductDao.getProductBySkuNumber(skuNumber)` → aggregate or null
- `TransactionDao.readTransactionWithItemsInRange(start, end)` → PagingSource
- `StockDao.readStockMovement()` → PagingSource; `getStockByProductId` → `BigDecimal`
- `BundleDao.insertBundleAndGetId`, `deleteBundleProductsByBundleId`, etc.

### Converters

`BigDecimalConverter`, `StockMovementTypeConverter`, `TransactionVariantConverter`
— Room type converters (KMP-aware).

### Repositories (`data/repository/`)

Each implements a `domain/repository/*Repository` interface:

| Impl | Interface | Highlights |
|---|---|---|
| `ProductRepositoryImpl` | `ProductRepository` | CRUD, SKU lookup, price history flows |
| `StockRepositoryImpl` | `StockRepository` | stock + movement writes, reads |
| `TransactionRepositoryImpl` | `TransactionRepository` | insert transaction (+ items), range reads, recap queries |
| `VariantRepositoryImpl` | `VariantRepository` | variant CRUD |
| `CategoryRepositoryImpl` | `CategoryRepository` | category CRUD |
| `BundleRepositoryImpl` | `BundleRepository` | bundle + bundle-product writes |

---

## Domain Layer: Components & State

The domain layer follows a **component/state/event** pattern (Decompose +
`Value`/`StateFlow`): each screen has a `*Component` interface, a
`*ComponentImpl` (state machine), and `*StateEvent` classes.

### Components (`domain/component/`)

| Component | Screens | Key responsibilities |
|---|---|---|
| `DashboardComponent` | Dashboard | hosts tab stack, drawer nav, product menu (export/import) |
| `TransactionAddComponent` | TransactionAdd | cart, product/bundle dialogs, search, QR |
| `TransactionProductConfigComponent` | TransactionProductConfig | amount/price/variant config per line item |
| `TransactionSearchComponent` | TransactionSearch | search + navigate to view |
| `TransactionViewComponent` | TransactionView | view transaction, save receipt, print |
| `TransactionRecapComponent` | TransactionRecap | date range, recap list, print recap |
| `TransactionRecapProductViewComponent` | TransactionRecapProductView | item detail |
| `TransactionHistoryComponent` | TransactionHistory | history list |
| `ProductListComponent` | ProductList | product list, categories, low stock, bundles, export/import |
| `ProductAddEditComponent` | ProductAddEdit | add/edit product + variants + prices |
| `ProductAddVariantsViewComponent` | VariantView | variant picker |
| `BundleAddEditComponent` | BundleAddEdit | add/edit bundle |
| `QrScannerComponent` | QrScanner | QR scan + callback |
| `ProductInboundOutboundComponent` | ProductInboundOutbound | stock in/out dialog |
| `SettingComponent` | Setting | settings state + updates |

Each `*Component` exposes `state: Value<*State>` (or `StateFlow`) and
`onEvent(*Event)`. The impls hold `MutableStateFlow`s, run business logic in a
`coroutineScope(Dispatchers.Main.immediate)`, and push results into state.

### State/Event classes (`domain/state_event/`)

- `DashboardComponentState.kt` — dashboard UI state (selected tab, menu open)
- `TransactionAddStateEvent.kt` — `TransactionAddState` (cart items, dialogs,
  search query) + `TransactionAddEvent` (search, tab, product config, dialogs)
- `TransactionRecapStateEvent.kt` — recap range + list state
- `TransactionSearchStateEvent.kt`, `TransactionViewStateEvent.kt`,
  `SettingStateEvent.kt` (settings state/event)

---

## The UI Layer

### Screens (`ui/screen/` — 53 files)

| Area | Files |
|---|---|
| Root | `DashboardScreen.kt`, `QrScannerScreen.kt`, `SettingScreen.kt`, `TransactionViewVariantSection.kt` |
| `bundle_add_edit/` | `BundleAddEditScreen.kt` (+ `component/` sub-screens) |
| `product_add/` | `ProductAddEditScreen.kt`, `AddImageButton.kt`, `ProductAddEditPriceHistory.kt`, `ProductAddVariantsViewScreen.kt` |
| `product_inbound_outbound/` | `ProductInboundOutboundScreen.kt`, `...Dialog.kt`, `...Item.kt` |
| `product_list/` | `ProductListScreen.kt` + `component/` (`Bundles.kt`, `Categorized.kt`, `LowStock.kt`, `ProductItem.kt`, `AddCategoryDialog.kt`, ...) |
| `transaction_add/` | `TransactionAddScreen.kt`, `TransactionAddCartContent.kt`, `TransactionAddProductGrid.kt`, `TransactionProductConfigScreen.kt` + `component/` (dialogs, cart, bundle, search bar) |
| `transaction_history/` | `TransactionHistoryScreen.kt`, `TransactionHistoryScreenHeader.kt`, `TransactionItem.kt` |
| `transaction_recap/` | `TransactionRecapScreen.kt` + `component/` (`TransactionRecapTabRow.kt`, `TransactionRecapProductOutbound.kt`) |
| `transaction_recap_product_view/` | item-detail screen |
| `transaction_search/` | `TransactionSearchScreen.kt` |
| `transaction_view/` | `TransactionViewScreen.kt` |

Screens are `@Composable fun XScreen(component: XComponent, modifier: Modifier)`
— they collect `component.state` via `collectAsStateWithLifecycle()` and call
`component.onEvent(...)`. They are **shared** (commonMain) and run on both
platforms through the platform bridges.

### Shared components (`ui/component/`)

- `TextField.kt` — `BorderedTextField` (two overloads: value-based and
  `TextFieldState`-based) with CharcoalBlue border + `Util.defaultShape`; used
  by product dialogs and the settings store-name field.
- `AnimatedIcon.kt` — animated icon wrapper.

### Theme (`ui/theme/`)

- `Color.kt` — Material 3 light-scheme role colors (`primaryLight`,
  `primaryContainerLight`, `surfaceContainerLight`, ...) and high-contrast
  variants (e.g. `primaryLightHighContrast = #51191B`).
- `ColorV2.kt` — **brand palette**: `BurgundyRed` (#9B2A31), `CharcoalBlue`
  (#233D4D), `MintCream` (#F0F7F4), `Cerulean`, `MutedTeal`, `PacificBlue`,
  `CoralGlow`, `GoldenSand`, plus stock-movement colors (`DeepTeal`, ...).
- `Theme.kt` — `AppTheme` (light scheme; `highContrastLightColorScheme` used by
  the active theme) + `MaterialExpressiveTheme` wrapper.
- `Type.kt` — typography.

> **Brand rule:** the app's primary accent is **BurgundyRed** (`#9B2A31`) — used
> for the logo, selected drawer item, switch tracks, and the save button in
> settings. `CharcoalBlue` is the secondary accent (borders, text fields).

---

## Platform Bridges (expect/actual)

All platform-specific behavior is abstracted behind `expect` declarations in
`commonMain` with `actual` implementations in `androidMain` / `jvmMain`:

| Bridge (common) | Android actual | JVM actual | Purpose |
|---|---|---|---|
| `PlatformUi` | `PlatformUi.android.kt` | `PlatformUi.jvm.kt` | platform UI affordances (bluetooth permission) |
| `PlatformBackHandler` | `.android.kt` | `.jvm.kt` | system back handling (Android `BackHandler`; JVM no-op) |
| `PlatformQrScanner` | `.android.kt` | (JVM no-op/absent) | QR scanning (qr-kit) |
| `CameraPermission` | `.android.kt` | `.jvm.kt` | camera permission state (permissions-compose) |
| `ImagePickHandler` | `.android.kt` | (JVM no-op) | image picker (FileKit) |
| `PlatformAppVersion` | `.android.kt` | `.jvm.kt` | app version string (Android `versionName`, JVM manifest `Implementation-Version`) |
| `PlatformExportNotification` | `.android.kt` | `.jvm.kt` | `rememberExportNotifier()` — post-export notification |
| `PhoneOnlyContent` | — (common) | — | gates UI to phone-sized windows (desktop hides UI) |
| `FileStorage` (`util`) | `.android.kt` | `.jvm.kt` | save images / public docs |
| `AppLogger` (`util`) | `.android.kt` | `.jvm.kt` | logging |
| `ImageBitmapExt` (`util`) | `.android.kt` | `.jvm.kt` | bitmap → PNG bytes |
| `Printer` | `.android.kt` | — | receipt printing (Android) |

> **Rule:** `commonMain` must stay free of Android-only libraries. Anything
> platform-specific goes through these bridges.

---

## Settings & DataStore

- **`SettingManager`** (`data/SettingManager.kt`) — DataStore-backed settings:
  - `isProductVolatile`, `isAmountPrecise`, `isCustomerNameNeeded`,
    `isTransactionRecapNeeded`, `isProductStockTracked`, `storeName`
  - `getSettings(): Flow<PosLeSettings>` + per-key `updateXxx(...)` suspend fns
- **`PosLeSettings`** data class (defaults all `false`, empty store name).
- **`SettingComponentImpl`** exposes `SettingState` + `SettingEvent`
  (`OnStoreNameSaved`, toggle events, etc.).
- **`SettingScreen.kt`** — the settings UI:
  - **About card** at top: app icon (`ic_app_logo.png`, the real POSLE logo),
    name "PosLe", "Versi %s" from `platformAppVersion()`, description.
  - **Store name row**: `BorderedTextField` bound to `storeNameState` + a
    BurgundyRed save `FilledIconButton`.
  - **Toggles** (via `SettingItem`): product volatile, customer name needed,
    transaction recap, stock tracked. (The "Jumlah Presisi" toggle was removed;
    `isAmountPrecise` still drives `TransactionProductConfigScreen`.)

---

## Key Flows

### 1. Transaction (add → config → QR → view)

```
TransactionAddScreen
  ├─ search bar / product grid (PagingSource via ProductRepository)
  ├─ product dialog  → OnRequestProductConfig(product)
  │    └─ TransactionProductConfigScreen
  │         ├─ amount (digitsOnly), price (RupiahVisualTransformation),
  │         ├─ variants (VariantView)
  │         └─ confirm → TransactionAddComponent (cart)
  ├─ bundle dialog  → OnShown(bundle) → quantity digitsOnly + unit price
  ├─ QR scanner → OnQrScanned → product lookup → config
  └─ checkout → TransactionRunner.withTransaction { }
       ├─ insert transaction + items (+ variant junctions)
       ├─ decrement stock + record StockMovement (OUT)
       └─ snackbar "Struk berhasil disimpan" / print receipt
```

### 2. Product management (list → add/edit → stock)

```
ProductListScreen (PagingSource, search, categories, low-stock, bundles)
  └─ ProductAddEditScreen
       ├─ image picker (ImagePickHandler → ImageBitmapExt → saveImage)
       ├─ name/SKU/unit/prices (Rupiah transformations)
       ├─ price history (ProductAddEditPriceHistory)
       └─ variants (ProductAddVariantsViewScreen) → save
ProductInboundOutboundScreen
  └─ ProductInboundOutboundDialog (+ ProductInboundOutboundItem)
       └─ stock in/out → StockDao + StockMovement (IN/OUT)
```

### 3. Recap & history

```
TransactionRecapScreen
  ├─ date range picker → readTransactionWithItemsInRange
  ├─ tab: summary / detail
  └─ print recap → onPrintRecap(state)
       ├─ Android: enqueuePdfExport (WorkManager) → PdfExportWorker
       │    → generateAsBytes → saveToPublicDocuments → NotificationHelper
       └─ Desktop: savePdfAndNotify → DesktopNotifier.notifyPdfGenerated
TransactionHistoryScreen → list → TransactionViewScreen (receipt, print)
TransactionSearchScreen → search → TransactionViewScreen
```

### 4. QR scanner

```
QrScannerScreen ← QrScannerComponentImpl
  ├─ CameraPermissionState (Android: permissions-compose; JVM: always granted)
  ├─ reads isGranted at composition time (rule!)
  └─ on result → onQrScanned callback → ProductAddEdit/TransactionAdd
```

---

## Export & Notification Flows

### Product list export (Excel)

```
DashboardScreen "Ekspor Produk" (ProductList dropdown)
  └─ ProductListComponentEvent.OnExportProducts { bytes ->
        val savedPath = fileStorage.saveToPublicDocuments("products.xlsx", "Daftar Produk", bytes)
        notifyExport("products.xlsx", savedPath)      // rememberExportNotifier()
     }
```

- `ExcelManager.exportProductsToExcel(List<Product>): ByteArray` (Apache POI,
  columns: ID, SKU, Name, Categories, Unit, Buy Price, Sell Price, Stock).
- `ExcelManager.importProductsFromExcel(bytes)` — import path (ProductList
  menu "Impor Produk" via `rememberFilePickerLauncher` + `readBytes`).
- Notification: `rememberExportNotifier()` (platform bridge) →
  Android `NotificationHelper.notifySuccess` (title **"Data Produk berhasil
  diekspor"**, tap opens file) / desktop `DesktopNotifier.notifyExportFinished`
  (tray popup). Failure (savedPath == null): desktop error popup, Android
  silent.

### PDF recap export

- Android: `MainActivity.enqueuePdfExport` → `PdfExportWorker` →
  `TransactionReportGenerator.generateAsBytes` → save → `NotificationHelper`
  (title "Laporan Selesai").
- Desktop: `savePdfAndNotify` in `desktopApp/main.kt` → `DesktopNotifier`.

### The bridges

| File (common) | Android actual | JVM actual |
|---|---|---|
| `ui/platform/PlatformExportNotification.kt` | `.android.kt` → `NotificationHelper` | `.jvm.kt` → `DesktopNotifier` |
| `util/NotificationHelper.kt` (androidMain) | mime-aware (PDF/XLSX), tappable intent | — |
| `util/DesktopNotifier.kt` (jvmMain) | — | system-tray popup |

---

## Utilities

| Utility | Purpose |
|---|---|
| `AppLogger` | expect/actual logger (Android `Log`, JVM `println`) |
| `FileStorage` | expect/actual file IO (`saveImage`, `saveToPublicPictures`, `saveToPublicDocuments`) |
| `ImageBitmapExt` | expect/actual `encodeToPngBytes` (Android Bitmap, JVM Skia) |
| `ExcelManager` | Apache POI xlsx export/import of products |
| `RupiahVisualTransformation` | IDR formatting in text fields (visual transformation + offset mapping) |
| `TextParse` | `BigDecimal.toRupiah()`, `toDisplayText()`, `String.digitsOnly()` (digits-only filter for qty/price inputs) |
| `TimeUtil` | today range, `startOfDay`/`endOfDay` epoch millis, `toLocalDate` |
| `FormatDate` | `Long.toFormattedDateTime()` / `toFormattedDate()` |
| `Util` | `defaultTextFieldColor()`, `defaultTransparentTextFieldColor()`, `defaultShape` (25F) |
| `Serializer` | serialization helpers |
| `Value` / `Aliases` | type aliases (`Name`, `Price`, `Amount`, `Unit`, `SkuNumber` — value classes) |
| `RunnableService` | `enqueue(id, data)` abstraction (Android WorkManager) |
| `PdfNotificationHandler` | fun interface for PDF-done callbacks |
| `SampleData` | sample/seed data for previews |
| `Coroutine` | coroutine helpers |

---

## Testing

- **JVM tests** (`shared/src/jvmTest/`) — run with `./gradlew :shared:jvmTest`:
  - `SharedLogicDesktopTest.kt` (smoke)
  - `TextParseTest.kt` — `digitsOnly()` edge cases (symbols, spaces, empty,
    order preservation)
  - `ProductRepositoryImplTest`, `BundleRepositoryImplTest`,
    `TransactionRepositoryImplTest` — Room-backed (in-memory `TestDatabase.kt`)
- **Android host tests** (`shared/src/androidHostTest/`) —
  `./gradlew :shared:testAndroidHostTest`
- **Full verification chain** used by the project:
  `:shared:compileKotlinJvm :shared:compileAndroidMain :shared:jvmTest
  :desktopApp:compileKotlin :androidApp:compileDebugKotlin
  :androidApp:assembleDebug`

---

## Conventions & Gotchas

1. **`rules/coding_rules.md`** — mandatory: naming, no deprecated APIs, tests
   for new logic (rule 8).
2. **All screens live in `shared/.../ui/screen/`** — `commonMain` must not
   import Android-only libraries. Platform features go through the
   expect/actual bridges (`PlatformUi`, `CameraPermissionState`,
   `rememberImagePickHandler`, `PlatformBackHandler`, `rememberExportNotifier`,
   `encodeToPngBytes`, `platformAppVersion`).
3. **Composable permission checks** must be read at composition time, **not**
   inside `onClick` lambdas.
4. **`google-services.json` is per build type** (`androidApp/src/debug/` =
   `posle-dev` / `org.lelestacia.posle.dev`; `androidApp/src/release/` =
   `posle-833f7` / `org.lelestacia.posle`) and is gitignored — a fresh clone
   must supply both files or `process*GoogleServices` fails.
5. **Phone gate**: `PhoneOnlyContent` (`maxWidth < 600.dp && maxHeight < 1000.dp`)
   hides the mobile UI on desktop (1280×800 window). Android `MainActivity` is
   not gated.
6. **Patching**: unique anchors only; use `git mv` for renames; `search_files`
   can be unreliable — prefer `grep`/`find` in a terminal.
7. **`digitsOnly()`** filters `filter { it.isDigit() }` — e.g.
   `"1-2.3, 4".digitsOnly()` → `"1234"`. Used by bundle qty/price inputs.
8. **Notifications** are cross-platform: Android uses
   `NotificationHelper` (+ `POST_NOTIFICATIONS` permission, requested at launch),
   desktop uses `DesktopNotifier` (system tray).
9. **Themes**: `AppTheme` uses the high-contrast light scheme; the brand accent
   is `BurgundyRed` (#9B2A31), secondary `CharcoalBlue` (#233D4D).
10. **Version strings**: Android `versionName` 0.2.5 (debug suffix " Dev");
    desktop `packageVersion` 1.0.0 (manifest `Implementation-Version`, fallback
    "1.0.0").

---

*Generated by Hermes Agent on 2026-08-02*
