# Physical

A fully-featured fitness companion app built with Jetpack Compose and Firebase — designed for Kenyan users. Tracks runs via GPS, detects daily activity, suggests Kenyan superfoods, and generates personalized sleep schedules.

## Features

### Authentication
- Email/password sign up and login
- Guest mode with data-loss warning dialog
- Session persistence across app restarts

### Dashboard
- Green gradient header with hamburger menu
- Welcome message with capitalized first name
- Live activity stats: step count, walking time (min), in-vehicle time (min)
- Today's runs and distance
- This Week card: runs completed, total km, total minutes
- Lifetime card: total runs, total km, average pace (min/km)
- Motivational quote card

### GPS Run Tracking
- Single unified Runs page (morning and evening merged)
- Big START button — tap to begin GPS tracking via FusedLocationProviderClient
- Real-time display: elapsed time, distance (km), pace (min/km)
- Pause / Resume mid-run
- Stop saves the run to Firestore with distance and duration
- Home address detection via reverse geocoding (Geocoder API)
- Suggested safe routes near your home: Short Loop (1km), Moderate Route (3km), Long Run (5km), Challenge (10km)
- Run history — last 10 runs listed with date, distance, and duration

### Sleep Schedule Questionnaire
- 5-step interactive questionnaire:
  1. Days per week you work
  2. Hours per day
  3. Work start time
  4. Commute time (shows a joke if > 60 min)
  5. Job title
- Generates a personalized sleep plan with suggested bedtime, wake time, and consistency score
- Saves schedule to Firestore for logged-in users

### Kenyan Foods & Nutrition
- 80+ items across 3 tabs: Foods (41), Snacks (20), Drinks (24)
- Each item includes: description, key nutrients, health benefits, preparation instructions
- Expandable cards with animated reveal for preparation steps
- Covers traditional Kenyan dishes, local snacks, and beverages

### Activity Tracking
- Real-time step counter using the device step sensor
- Walking time detection via Google Activity Recognition transitions
- In-vehicle time detection via Activity Recognition
- Automatically starts/stops with the app lifecycle

### Progress & Settings
- K/D ratio-style stats: Total Runs, Total Km, Avg Pace (min/km), Best Run (km)
- Weekly stats card: runs and distance this week
- Settings section with permission toggles:
  - Location Access (for GPS tracking)
  - Activity Recognition (for step counting and movement detection)
- Consistency tips card

### Sidebar Navigation
- Professional green gradient header with app branding
- Close button (X) top-right
- Five navigation items with unicode icons and active state highlighting (green background + bold)
- User info at bottom: first name (bold), full name below
- Gray "Log out" button at the very bottom

### Splash Screen
- Green gradient background with animated scaling fitness quote
- Auto-dismisses after 2.5 seconds
- Random quote selection on each launch

## Screenshots

<!-- TODO: Add screenshots -->
| Dashboard | Run Tracking | Sleep Questionnaire | Kenyan Foods |
|-----------|-------------|-------------------|--------------|
|           |             |                   |              |

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material3 |
| Navigation | Navigation Compose |
| Architecture | ViewModel + StateFlow |
| Backend | Firebase Auth + Firestore + Analytics |
| Location | Google Play Services Location (FusedLocationProviderClient) |
| Activity | Google Activity Recognition API |
| Build | Gradle with Version Catalog (libs.versions.toml) |
| Min SDK | 24 |
| Target SDK | 34 |

## Permissions

- `INTERNET` — Firebase communication
- `ACCESS_FINE_LOCATION` — GPS run tracking
- `ACCESS_COARSE_LOCATION` — Fallback location
- `ACTIVITY_RECOGNITION` — Step counter + movement detection

## Setup

### Prerequisites
- Android Studio Hedgehog or later
- A physical Android device (API 24+) — GPS and step sensor required for full functionality
- A Firebase project

### Firebase Configuration
1. Go to [Firebase Console](https://console.firebase.google.com) and create a project
2. Add an Android app with package name `com.example.physical`
3. Download `google-services.json` and place it in `app/`
4. Enable **Email/Password** sign-in method under Authentication > Sign-in method
5. Enable **Firestore Database** in production mode
6. (Optional) Enable **Google Analytics** for crash reporting

### Build & Run
1. Open the project in Android Studio
2. Sync Gradle (File > Sync Project with Gradle Files)
3. Connect your device or start an emulator
4. Run `./gradlew assembleDebug` or press the Run button

### Troubleshooting
- **google-services.json missing**: The app will compile but crash on startup. Ensure the file is in `app/`.
- **Location permissions**: The app requests location and activity recognition permissions on first launch. Denying will disable GPS tracking and step counting.
- **Duplicate class errors**: Run `./gradlew clean` if you see dex merge failures.

## Build

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project Structure

```
app/src/main/java/com/example/physical/
├── MainActivity.kt              # Entry point, splash, tracker init, permissions
├── data/
│   ├── model/
│   │   ├── Run.kt               # Run data model
│   │   ├── SleepSchedule.kt     # Sleep plan data model
│   │   ├── FoodSuggestion.kt    # Food item data model
│   │   └── ActivityData.kt      # Daily activity snapshot
│   ├── repository/
│   │   ├── FitnessRepository.kt # Firestore CRUD for runs & sleep
│   │   ├── RunTracker.kt        # FusedLocationProviderClient GPS logic
│   │   ├── HomeLocationManager.kt # Reverse geocoding + route suggestions
│   │   ├── ActivityTracker.kt   # Step sensor + Activity Recognition
│   │   └── FoodData.kt          # 80+ hardcoded food items
│   └── theme/
│       └── Theme.kt             # Material3 color scheme
├── ui/
│   ├── auth/                    # LoginScreen, SignUpScreen, AuthViewModel
│   ├── home/                    # Dashboard + HomeViewModel
│   ├── runs/                    # RunTrackingScreen + RunViewModel
│   ├── sleep/                   # SleepScreen + SleepViewModel
│   ├── nutrition/               # NutritionScreen with tabs
│   ├── progress/                # ProgressScreen with stats + settings
│   ├── navigation/              # AppNavigation with drawer + NavHost
│   └── splash/                  # SplashScreen with animated quote
```

## Contributing

This is a personal project. Pull requests and suggestions are welcome via GitHub Issues.

## License

MIT
