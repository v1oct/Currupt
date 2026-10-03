# CURRUPT Gaming — Core Architecture Specification

This document details the architecture for **CURRUPT Gaming**, establishing the foundational core package structure, data models, state management, remote configuration layer, Game Registry system, and Tool Registry system.

---

## 1. Package Structure

All new core packages reside under the `com.currupt.reflame` namespace:

```
com.currupt.reflame/
├── core/
│   ├── config/       # Local application defaults & config state (ClientDefaults, ConfigManager)
│   ├── model/        # Foundational domain data models (Game, GameProfile, Tool)
│   └── state/        # Application-level state model (AppState)
├── client/
│   └── model/        # Client runtime models & flags (ClientConfig, FeatureFlag)
├── account/
│   └── model/        # User entitlement & account models (UserEntitlement)
├── admin/
│   └── model/        # Administrative permission models (AdminPermission)
├── remote/
│   ├── data/         # Data sources (RemoteConfigDataSource, LocalRemoteConfigDataSource)
│   ├── repository/   # Repositories (RemoteConfigRepository, DefaultRemoteConfigRepository)
│   └── model/        # Remote payload wrappers (RemoteResponse)
└── feature/
    ├── games/        # Game registry & feature state (GameRepository, GameRegistry, GameManager)
    ├── tools/        # Tool registry & feature state (ToolRepository, ToolRegistry, ToolManager, ToolAvailability)
    ├── performance/  # Performance monitoring feature state
    └── profile/      # User profile feature state
```

---

## 2. Foundational Data Models

### 1. `Game` (`core/model/Game.kt`)
Represents a supported game entry.
- `id`: Stable unique identifier.
- `displayName`: Display name of the game.
- `packageNames`: List of target Android package names.
- `iconUrl` / `brandingUrl`: Asset & branding references.
- `isEnabled`: Availability toggle.

### 2. `GameProfile` (`core/model/GameProfile.kt`)
Represents game-specific configuration and active tool profiles.
- `gameId`: Reference to parent `Game`.
- `enabledTools`: Enabled tool identifiers for this game profile.
- `configurationValues`: Key-value configuration payload (`JsonObject`).
- `displayMetadata`: Display metadata payload (`JsonObject`).

### 3. `Tool` (`core/model/Tool.kt`)
Represents utility or optimization tools available to games.
- `id`: Stable tool identifier.
- `displayName`: Display title.
- `description`: Overview description.
- `category`: Category classification (`ToolCategory`: PERFORMANCE, GRAPHICS, UTILITY, NETWORK, SYSTEM).
- `isEnabled`: Global tool availability toggle.
- `isPremium`: Tier restriction flag.

### 4. `FeatureFlag` (`client/model/FeatureFlag.kt`)
Simple key-value feature toggling mechanism.
- `key`: Identifier string.
- `enabled`: Boolean toggle state.

### 5. `ClientConfig` (`client/model/ClientConfig.kt`)
Configuration payload holding app-wide dynamic parameters.
- `maintenanceState`: `MaintenanceState` (isUnderMaintenance, message, allowedRoles).
- `currentConfiguration`: Dynamic settings payload (`JsonObject`).
- `featureFlags`: List of `FeatureFlag` items.
- `announcementsConfig`: `AnnouncementsConfig` settings.
- `brandingAssets`: `BrandingAssets` (logoUrl, splashImageUrl, accentColorHex, loadingAnimationUrl).

### 6. `UserEntitlement` (`account/model/UserEntitlement.kt`)
Enumeration defining tier access levels:
- `FREE`
- `PREMIUM`

### 7. `AdminPermission` (`admin/model/AdminPermission.kt`)
Role permissions enum for administrative access:
- `OWNER`
- `ADMIN`
- `DEVELOPER`
- `MODERATOR`

### 8. `AppState` (`core/state/AppState.kt`)
Pure data model representing application state for `StateFlow`:
- `isLoading`: Global loading state.
- `clientConfig`: Current `ClientConfig` (defaults to `ClientDefaults.DEFAULT_CLIENT_CONFIG`).
- `userEntitlement`: Current user's `UserEntitlement`.
- `activeGame`: Currently selected `Game`.
- `errorMessage`: Active error message, if any.

---

## 3. Remote Configuration Flow

The remote configuration system decouples dynamic runtime settings from application UI components.

```
UI / ViewModel
    ↓
ConfigManager (core/config/ConfigManager.kt)
    ↓
RemoteConfigRepository (remote/repository/RemoteConfigRepository.kt)
    ↓
RemoteConfigDataSource (remote/data/RemoteConfigDataSource.kt)
    ↓
Backend / Local Fallback (remote/data/LocalRemoteConfigDataSource.kt)
```

---

## 4. Game Registry Architecture

Game management uses a decoupled registry/repository pattern to avoid conditional branching (`if game == X else if game == Y`).

```
Game (core/model/Game.kt)
    ↓
GameRepository (feature/games/GameRepository.kt)
    ↓
GameRegistry (feature/games/GameRegistry.kt)
    ↓
GameManager (feature/games/GameManager.kt)
    ↓
Future ViewModels / UI
```

---

## 5. Tool Registry Architecture

The Tool registry system manages tools as independent capability definitions decoupled from specific games or UI composables.

```
Tool (core/model/Tool.kt)
    ↓
ToolRepository (feature/tools/ToolRepository.kt)
    ↓
ToolRegistry (feature/tools/ToolRegistry.kt)
    ↓
ToolManager (feature/tools/ToolManager.kt)
    ↓
Future ViewModels / UI
```

### Components & Roles

1. **`ToolRepository` (`feature/tools/ToolRepository.kt`)**: Interface defining catalog queries (`getTools()`, `getTool(id)`).
2. **`LocalToolRepository` (`feature/tools/LocalToolRepository.kt`)**: Catalog implementation containing initial tool definitions (`fps_monitor`, `fps_cap`, `motion_blur`, `black_screen`, `cps_counter`, `audio_boost`, `device_cooler`).
3. **`ToolRegistry` (`feature/tools/ToolRegistry.kt`)**: Central registry supporting tool registration and lookup by ID.
4. **`ToolAvailabilityResolver` & `ToolAvailability` (`feature/tools/ToolAvailability.kt`)**: Evaluates tool status (`AVAILABLE`, `DISABLED_GLOBALLY`, `UNAVAILABLE_FOR_GAME`, `REQUIRES_PREMIUM`) using dynamic configuration (`GameProfile`, `UserEntitlement`), without hardcoding game names or UI logic.
5. **`ToolManager` (`feature/tools/ToolManager.kt`)**: StateFlow manager for available tools, selected tool, availability mappings, loading state, and errors.

---

## 6. Architectural Rules & Design Constraints

1. **Pure Data Models**: Models contain Kotlin data structures only. No Jetpack Compose, UI rendering, Android Context, or Activity references.
2. **Decoupled Data Layer**: No direct Supabase, HTTP, or database calls inside model classes.
3. **Serialization**: Standard `@Serializable` annotations (Kotlinx Serialization) applied to domain models.
4. **Remote Control Scope**: Remote configuration controls dynamic parameters (maintenance mode, feature flags, announcements, branding assets, custom settings).
5. **Game & Tool Independence**:
   - A `Game` object describes game metadata only.
   - A `Tool` object describes capability metadata only.
   - Tool-game availability is driven by configuration (`GameProfile.enabledTools`), strictly forbidding hardcoded game checks (`if game == ...`).
6. **No Fake Executable Capabilities**: Catalog entries represent tool capability definitions only; low-level hardware or overlay execution logic will be integrated via dedicated system drivers in future phases.
