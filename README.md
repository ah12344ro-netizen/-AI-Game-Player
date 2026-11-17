# AITouchGamer

Android-first, offline-first game assistant. Initial game profile: **STS2 — Slay the Spire 2**.

## Included
- Native Kotlin + Jetpack Compose Android application.
- STS2 profiles for the five main-branch characters: Ironclad, Silent, Regent, Necrobinder and Defect.
- Separate experimental profile for The Fool public beta.
- Locally persisted character and play-style preferences.
- Offline tactical baseline using manually entered HP, energy, block, incoming damage, enemy HP and hand cards.
- Board calibration form for the user's selected mobile board. Coordinates remain blank until calibrated.
- Unit tests and GitHub Actions build/test workflow.

## Current limitations
This repository contained only a README before this Android project was added. The earlier AITouchGamer source archive was not available in this workspace. This is a clean starter project, not a verified merge of the previous touch automation source.

This build does not yet capture the game screen, run OCR/object detection, use Android Accessibility gestures, or automatically play the game. It contains no trained model weights. Tactical advice currently uses manually entered values. Board calibration stores user-entered percentages but is not connected to a touch executor.

## Structure
- game/: game state and offline tactical reasoning.
- data/: local preferences and board calibration persistence.
- ui/: STS2 coaching and board calibration screens.
- assets/game_profiles/: game knowledge kept separate from board calibration.

## Build
GitHub Actions uses JDK 17, Android SDK and Gradle 8.9. Workflow: .github/workflows/android.yml. Expected artifact: AITouchGamer-debug-apk.

## STS2 knowledge
The profile is a conservative baseline based on official Mega Crit information. Early Access can change content and balance; card values, enemy intents, relic effects and route heuristics must be versioned against the exact game build before being treated as authoritative. The Fool is isolated in a separate public-beta profile.
