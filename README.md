# Physical

A fitness companion app built with Jetpack Compose and Firebase — designed for Kenyan users.

## Features

- **Splash Screen** — Animated green gradient with random fitness quotes
- **Authentication** — Email/password login & signup with guest mode (data-loss warning)
- **Dashboard** — Green gradient header with activity stats (steps, walking, vehicle), today's runs, weekly progress, lifetime totals, and motivational quote
- **GPS Run Tracking** — Start/Stop with real-time distance, duration, pace. Pause/resume support. Home address detection via reverse geocoding with suggested safe routes near you
- **Sleep Schedule** — 5-step questionnaire (days/week, hours, start time, commute, job title) with personalized sleep plan
- **Kenyan Foods** — 80+ traditional foods, snacks, and drinks across 3 tabs with nutrition info, health benefits, and preparation instructions
- **Progress & Settings** — Overall stats (Total Runs, Total Km, Avg Pace, Best Run) like a K/D ratio, weekly stats, plus permission toggles for Location and Activity Recognition
- **Activity Tracking** — Real-time step counter and movement detection via Google Activity Recognition
- **Sidebar Navigation** — Professional drawer with icons, active state highlighting, green header, and logout at bottom

## Tech Stack

- Jetpack Compose (Material3)
- Firebase Auth + Firestore + Analytics
- Google Play Services Location (FusedLocationProviderClient)
- Google Activity Recognition API
- Navigation Compose + ViewModel + StateFlow

## Setup

1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app with package name `com.example.physical`
3. Download `google-services.json` and place it in `app/`
4. Enable Email/Password authentication in Firebase Console
5. Open the project in Android Studio and sync Gradle
6. Build and run on a device (API 24+)
