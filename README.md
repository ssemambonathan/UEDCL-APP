UEDCL Android App
**Uganda Electricity Distribution Company Limited**
Commercial-grade mobile platform — Kotlin + Jetpack Compose

---

## Quick Start

### 1. Firebase Setup (required before building)
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create project → Add Android app → package: `com.uedcl.app`
3. Download `google-services.json` → place in `app/` folder
4. Enable these Firebase services:
   - **Authentication** → Email/Password + Phone
   - **Firestore** → Start in test mode
   - **Realtime Database** → Start in test mode
   - **Cloud Messaging** (FCM) → auto-enabled

### 2. Apply Security Rules
- Copy `firebase/firestore.rules` → Firestore → Rules tab
- Copy `firebase/realtime-database.rules.json` → RTDB → Rules tab

### 3. Build
```bash
./gradlew assembleDebug
```

---

## Project Structure
```
app/src/main/java/com/uedcl/app/
├── MainActivity.kt              # Entry point, splash, nav host
├── UedclApplication.kt          # Hilt app class
├── auth/
│   └── AuthScreens.kt           # Login, Register, OTP, ForgotPassword
├── dashboard/
│   └── DashboardScreen.kt       # Main dashboard + bottom nav
├── yaka/
│   └── BuyYakaScreen.kt         # Prepaid token purchase + token display
├── payments/
│   └── PaymentsScreens.kt       # Payment methods + transaction history
├── usage/
│   └── UsageScreen.kt           # Analytics + MPAndroidChart bar/line charts
├── support/
│   └── SupportScreen.kt         # Fault reporting, ticket tracking, contacts
├── profile/
│   └── ProfileScreen.kt         # User profile, settings, dark mode, logout
├── navigation/
│   └── NavGraph.kt              # All routes + Screen sealed class
├── data/
│   ├── models/Models.kt         # All data classes + sealed UiState
│   ├── api/ApiService.kt        # Retrofit interface + client setup
│   ├── firebase/FirebaseHelper.kt  # Auth, Firestore, RTDB helpers
│   └── repository/Repository.kt # Auth, Meter, Payment, Support repos
├── viewmodels/
│   └── ViewModels.kt            # Auth, Dashboard, Yaka, Payments, Usage, Support VMs
├── services/
│   ├── SmartMeterService.kt     # Live meter simulation + Firebase push
│   └── UedclMessagingService.kt # FCM push notification handler
├── di/
│   └── AppModule.kt             # Hilt dependency injection
└── ui/
    ├── theme/Theme.kt           # Material3 colors, dark mode
    └── components/Components.kt # Circular indicator, buttons, text fields, badges
```

---

## Architecture
```
┌─────────────────────────────────────────┐
│  UI Layer (Jetpack Compose Screens)      │
│  StateFlow collected via collectAsState  │
├─────────────────────────────────────────┤
│  ViewModel Layer (HiltViewModel)         │
│  Business logic, UiState emission        │
├─────────────────────────────────────────┤
│  Repository Layer (single source)        │
│  API-first → Firestore fallback          │
├────────────────┬────────────────────────┤
│  Retrofit API  │  Firebase              │
│  (REST calls)  │  Auth/Firestore/RTDB   │
└────────────────┴────────────────────────┘
         ↑ Smart Meter Simulation
         SmartMeterService (coroutine)
         drains units, pushes to RTDB
```

## Smart Meter Data Flow
```
Physical Meter → REST API → Cloud Function → Firebase RTDB
                                                    ↓
                                          SmartMeterService
                                          (StateFlow → UI)
```
*In simulation mode, SmartMeterService drains units at ~0.008 kWh/min
with 1.5× multiplier during peak hours (6–9 AM, 6–9 PM).*

---

## Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| Jetpack Compose BOM | 2024.09.03 | UI |
| Firebase BOM | 33.4.0 | Auth, Firestore, RTDB, FCM |
| Hilt | 2.52 | Dependency injection |
| Retrofit | 2.11.0 | REST API |
| MPAndroidChart | 3.1.0 | Bar + line charts |
| Navigation Compose | 2.8.3 | In-app navigation |
| Coroutines | 1.9.0 | Async / StateFlow |
| Coil | 2.7.0 | Image loading |
| Accompanist | 0.36.0 | System UI controller |
| SplashScreen | 1.0.1 | Android 12+ splash |

---

## Color Palette
| Token | Hex | Use |
|---|---|---|
| UedclGreen | `#1B7A34` | Primary, buttons, icons |
| UedclGreenDark | `#0F5222` | Dark variant |
| UedclGreenSurface | `#E8F5ED` | Card backgrounds |
| UedclAmber | `#FFB300` | Notifications, badges |

---

## Firestore Schema
```
users/{uid}
  name, email, phone, district
  meters[]: {meterNo, address, tariffType, isPrimary}
  notifications/{id}: {title, message, type, isRead, timestamp}

meters/{meterNo}
  customerName, currentUnits, status, tariffType
  dailyAvgKwh, monthlyKwh, nextReadingDate
  lastRechargeAmount, lastRechargeDate, lastRechargeToken
  transactions/{txId}: {amount, unitsAdded, token, paymentMethod, status}

fault_tickets/{id}
  meterNo, area, type, description, status, reference, submittedAt
```
