[![CI](https://github.com/wafflestudio/SNUTT-android/actions/workflows/ci.yml/badge.svg)](https://github.com/wafflestudio/SNUTT-android/actions/workflows/ci.yml)
[![CD](https://github.com/wafflestudio/SNUTT-android/actions/workflows/cd.yml/badge.svg)](https://github.com/wafflestudio/SNUTT-android/actions/workflows/cd.yml)
<div align="center">
  <a href="https://github.com/wafflestudio/snutt-android">
    <img src="https://user-images.githubusercontent.com/33917774/199519767-60590904-b15a-4464-ab21-e3a424649d5c.svg" alt="Logo" width="70" height="70">
  </a>
  <h3 align="center">SNUTT Android</h3>
  <p align="center">
    The best timetable application for SNU students, developed and maintained by SNU students.
    <div style=" padding-bottom: 1rem;">
      <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
      <img src="https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white" />
      <img src="https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black" />
      <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white" />
    </div>
  </p>
</div>

## Features
- Abbreviated course search
- Course search by time slot
- Courses of interest
- Course review
- Vacancy notifications
- Friend's timetable
- Custom timetable theme

## Tech Stack
- MVVM
- Jetpack Compose
- Dependency Injection with Hilt
- Integration with React Native
- Firebase crashylistics & app distribution

## Getting Started
### Secrets
The following files are required for Staging build:
  - `app/src/staging/google-services.json`
  - `app/src/staging/res/value/strings.xml`

The following files are required for Live build:
  - `app/src/live/google-services.json`
  - `app/src/live/res/value/strings.xml`

Please request these files from the maintainer if needed.

### Installation
1. Install [Android Studio](https://developer.android.com/studio).
2. Clone the repository and open the project in Android Studio by entering the following command:
   ```
   git clone https://github.com/wafflestudio/snutt-android
   ```
3. Build and run the project using JDK 17.

### Deployment
1. Create a `release-${version-code}` branch and modify the version code in `version.properties`.
2. Upload the APK file to the Play Store.
3. Merge the `release-${version-code}` branch into the `develop` branch and create a new tag and release for the new version.

## Branch Conventions
- The default branch is `develop`.
- PR branch names should follow the format `${username}/${changes}` (e.g., `sanggggg/renewal-table-ui`).
- Only use squash merge when merging.

## Maintainers
- [@JuTaK97](https://github.com/JuTak97)
- [@eastshine2741](https://github.com/eastshine2741)
