# AITouchGamer — STS2 development branch

This branch adds an isolated offline STS2 tactical subsystem to the current repository while preserving the existing starter app.

## Work added
- Typed STS2 character, card, enemy-intent and combat-state models.
- Explainable local turn planner with energy limits, lethal prioritisation, enemy Block, Vulnerable and Weak projections.
- Confidence-gated OCR evidence adapter for explicitly labelled HP, max HP, energy and Block. Low-confidence readings are withheld.
- Unit tests for tactical planning and OCR evidence validation.
- Conservative roster metadata that keeps experimental/beta characters separate and avoids unverified HP/card values.

## Original application source status
The uploaded `AITouchGamer_hybrid_source.zip` has been located and inspected. It contains the fuller Android application: floating controller, MediaProjection capture, OpenCV/ML Kit OCR, TensorFlow Lite detector interface, Accessibility gestures, Room-based per-game memory, playbooks, and offline Arabic/English coaching.

**That archive has not yet been imported into this GitHub branch.** The current CI still builds the starter Android app plus the isolated STS2 module. This branch is not yet the integrated original application and does not produce a new APK containing all original features.

## STS2 integration status
The planner accepts structured card and enemy properties. The OCR adapter currently extracts only high-confidence, explicitly labelled player HP, max HP, energy and Block. It does not yet identify card names/costs/effects, enemy intents/HP, relics, Regent Stars, Defect Orbs, Necrobinder/Osty state, or The Fool-specific state from the game screen. It is not connected to automatic card-touch execution.

Unknown state is not guessed. The next integration step is to import the original source tree, connect its `FrameAnalysis` OCR output to this evidence layer, then add game-specific screen regions and a verified card/enemy knowledge pack.

## Build and validation
GitHub Actions builds the current repository and runs JVM unit tests on pull requests. A successful build of this branch must not be interpreted as validation of the unimported original archive.
