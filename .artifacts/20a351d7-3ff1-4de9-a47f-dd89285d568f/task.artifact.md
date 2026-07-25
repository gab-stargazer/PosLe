# Task List - Refactor Amount to BigDecimal & Room Migration

- `[x]` Common Utilities Refactor
    - `[x]` Refactor `Amount` value class in `Value.kt`
    - `[x]` Add `Double` converter to `BigDecimalConverter.kt`
- `[x]` Database Schema & Migration
    - `[x]` Update `StockDao.kt` methods
    - `[x]` Update `PosLeDB.kt` version and implement `MIGRATION_2_3`
- `[x]` Logic & UI Updates
    - `[x]` Update `ProductWithVariantsAndStock.kt` summation
    - `[x]` Update validation in `TransactionAddStateEvent.kt`
    - `[x]` Refactor `TransactionProductConfigComponentImpl.kt` amount handling
    - `[x]` Update `TransactionRepositoryImpl.kt` stock movement logic
    - `[x]` Fix display logic in `ProductInboundOutboundItem.kt`
