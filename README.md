# Physical-Fitness

A fully fledged Android fitness app built with Jetpack Compose and Firebase.

## Features

- **Splash Screen** - Animated launch screen with random fitness quotes
- **Authentication** - Email/password login & signup with guest mode
- **Activity Tracking** - Real-time step counter, walking and in-vehicle time detection using Google Activity Recognition
- **Run Tracking** - Log morning and evening runs with distance and duration
- **Sleep Schedule Questionnaire** - Personalized sleep recommendations based on your work days, hours, commute time, and job type
- **Kenyan Superfoods** - Nutrient-rich traditional Kenyan food suggestions
- **Progress Dashboard** - Track weekly runs, distance, and consistency
- **Sidebar Navigation** - Clean drawer with close button, user info, and logout

## Tech Stack

- Jetpack Compose (Material3)
- Firebase Authentication
- Firebase Firestore
- Firebase Analytics
- Google Activity Recognition API
- Navigation Compose
- ViewModel + StateFlow

## Setup

1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app with package name `com.example.physical`
3. Download `google-services.json` and place it in `app/`
4. Enable Email/Password authentication in Firebase Console
5. Open the project in Android Studio and sync Gradle
6. Build and run on a device (API 24+)
