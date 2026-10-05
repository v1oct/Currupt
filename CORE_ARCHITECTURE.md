# CURRUPT Gaming — Core Architecture Specification

This document details the architecture for **CURRUPT Gaming**, establishing the foundational core package structure, data models, state management, remote configuration layer, persistent cache & synchronization flow, configuration publishing lifecycle, backend access boundaries, Game Registry system, Tool Registry system, Game Profile & Exact Tool Assignment system, Roblox Game Detection system, Client Runtime & Edge Panel Overlay system, V1 In-Game Client Panel UI system, Account & Entitlement system, and Advanced Operational Modes.

---

## 1. Package Structure

All new core packages reside under the `com.currupt.reflame` namespace:

```
com.currupt.reflame/
├── core/
│   ├── config/       # Local application defaults, ConfigManager, & ConfigStatus
│   │   └── cache/    # Config cache abstraction (ConfigCache, FileConfigCache, InMemoryConfigCache)
│   ├── model/        # Foundational domain data models (Game, GameProfile, Tool)
│   ├── overlay/      # Overlay runtime & Compose views (OverlayController, AndroidOverlayController, OverlayService, EdgeLauncherOverlayView, CurruptEdgePanelContent, CurruptPanelState, ServiceLifecycleOwner)
│   └── state/        # Application-level state model (AppState)
├── client/
│   ├── model/        # Client runtime models (ClientConfig, ConfigMetadata, ClientConfigValidator, OperationalMode, OperationalModeConfig, FeatureFlag)
│   └── runtime/      # Client runtime state & managers (ClientRuntimeManager, ClientRuntimeState, ClientRuntimeStatus, OverlayPermissionChecker, PermissionStatus)
├── account/
│   ├── model/        # User entitlement & account models (Account, AccountStatus, UserEntitlement)
│   ├── repository/   # Repositories (AccountRepository, LocalAccountRepository)
│   ├── state/        # Account state (AccountState: Loading, Unauthenticated, Authenticated, Error)
│   └── AccountManager.kt # Central StateFlow account manager
├── admin/
│   ├── model/        # Admin permissions & envelopes (AdminPermission, ConfigEnvelope, ConfigLifecycleStatus, PublishResult)
│   ├── publisher/    # Configuration publishing boundary (ConfigurationPublisher, DefaultConfigurationPublisher)
│   └── data/         # Admin data sources (AdminConfigurationDataSource, LocalAdminConfigurationDataSource)
├── remote/
│   ├── data/         # Read-only client data sources (PublishedConfigDataSource, LocalPublishedConfigDataSource, RemoteConfigDataSource)
│   ├── repository/   # Repositories (RemoteConfigRepository, DefaultRemoteConfigRepository)
│   └── model/        # Remote payload wrappers (RemoteResponse)
└── feature/
    ├── games/        # Game registry, profile repositories, detection, & feature state
    │   ├── detection/ # Foreground application detection (GameDetector, AndroidUsageStatsDetector, NoOpGameDetector, GameDetectionManager, GameDetectionResult)
    │   └── GameRegistry.kt, GameProfileRepository.kt, GameManager.kt, CurruptGamesScreen.kt
    ├── tools/        # Tool registry & feature state (ToolRepository, ToolRegistry, ToolManager, ToolAvailability, ToolAvailabilityResolver)
    ├── home/         # V1 Home Screen (CurruptHomeScreen.kt)
    ├── settings/     # V1 Settings Screen (CurruptSettingsScreen.kt)
    ├── performance/  # Performance monitoring feature state
    └── profile/      # User profile feature state
```

---

## 2. Foundational Data Models

### 1. `Game` (`core/model/Game.kt`)
Represents a supported game entry.
- `id`: Stable unique identifier.
- `displayName`: Display name of the game.
- `packageNames`: List of target Android package names (e.g. `["com.roblox.client"]`).
- `iconUrl` / `brandingUrl`: Asset & branding references.
- `isEnabled`: Availability toggle.
- `isV1Supported`: V1 runtime support flag (`true` for Roblox, `false` for placeholders).

### 2. `GameProfile` (`core/model/GameProfile.kt`)
Explicit configuration source of truth for a game entry.
- `gameId`: Stable target `Game` ID.
- `enabledTools`: String list of assigned tool IDs (`List<String>`). Stores string references only, never `Tool` objects.
- `configurationValues`: Key-value configuration payload (`JsonObject`).
- `displayMetadata`: Display metadata payload (`JsonObject`).
- `profileVersion`: Schema version counter (`Long`).
- `isEnabled`: Game profile active toggle (`Boolean`).

### 3. `Tool` (`core/model/Tool.kt`)
Represents utility or optimization tools available to games.
- `id`: Stable tool identifier.
- `displayName`: Display title.
- `description`: Overview description.
- `category`: Category classification (`ToolCategory`: PERFORMANCE, GRAPHICS, UTILITY, NETWORK, SYSTEM).
- `isEnabled`: Global tool availability toggle.
- `isPremium`: Tier restriction flag.

---

## 3. V1 Edge Panel & Tool UI Architecture

The V1 Edge Panel transforms the overlay runtime into a real in-game client interface rendered in Jetpack Compose over Android `WindowManager`.

```
ClientRuntimeManager (client/runtime/ClientRuntimeManager.kt)
    ↓
OverlayService (android.app.Service)
    ↓
WindowManager.addView()
 ├── EdgeLauncherOverlayView (Vertical Handle)
 └── ComposeView (CurruptEdgePanelContent.kt)
      ├── Panel Header (Title: CURRUPT, Active Game: ROBLOX, Status: V1 READY)
      ├── Navigation Tabs (HOME, TOOLS, VISUALS, AUDIO, SETTINGS)
      ├── HOME Tab (Dashboard cards: Game, Runtime, Tools Available, Account Tier)
      ├── TOOLS Tab (Tool cards: fps_monitor, motion_blur, cps_counter + Search)
      ├── MOTION BLUR Detail View (In-app visual layer sliders: Intensity & Duration)
      ├── VISUALS Tab (Visual tools)
      ├── AUDIO Tab (Audio tools empty state)
      └── SETTINGS Tab (Overlay opacity slider, compact mode toggle, close panel)
```

### Components & UI Behaviors

1. **`CurruptEdgePanelContent` (`core/overlay/CurruptEdgePanelContent.kt`)**: Compose overlay UI rendered inside `OverlayService` via `ComposeView` with `ServiceLifecycleOwner`.
2. **Roblox V1 Tools**:
   - **`fps_monitor`**: Toggles a floating client-side performance widget (`60 FPS`).
   - **`motion_blur`**: Opens Motion Blur Detail View configuring client-side visual layer intensity (`0.0`–`1.0`) and duration (`100ms`–`500ms`).
   - **`cps_counter`**: Toggles a floating client-side click counter widget (`0 CPS`).
3. **Category Navigation Tabs**:
   - `HOME`: Compact client status dashboard.
   - `TOOLS`: Main tools list with real-time search filtering by name, description, or category.
   - `VISUALS`: Visual category tools (`motion_blur`).
   - `AUDIO`: Polite empty state for audio tools.
   - `SETTINGS`: Overlay panel opacity and compact preferences.
4. **Tool Availability & Entitlement Rules**:
   - Tools are dynamically sourced from `ToolManager` -> `ToolAvailabilityResolver` -> `GameProfile.enabledTools`.
   - Free users see premium lock badges for `isPremium` tools (`REQUIRES_PREMIUM`).
   - Premium users receive gold accent highlights (`#D4AF37`) throughout the panel UI.
5. **Operational Mode Integration**:
   - Respects `ConfigManager.operationalModeConfig`: displays operational mode notice banners for `MAINTENANCE` or `DOWNTIME` and disables restricted runtime actions for `EMERGENCY` or `UPDATE_REQUIRED`.
6. **Low-End Device Optimization**:
   - Built with lightweight Compose layout primitives. Avoids continuous expensive real-time blurs, heavy animations, or background video processing to maintain fast, responsive panel open/close operations.
7. **Security & Anti-Exploitation Policy (STRICT)**:
   - **Legitimate Client-Side Utilities Only**: Tools operate strictly as client-side overlay/visual features.
   - **NO Game Modification**: Does NOT modify, hook, memory-scan, inspect, manipulate files, or inject code into Roblox or any external game process.
