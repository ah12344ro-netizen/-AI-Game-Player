# AITouchGamer — STS2 tactical engine work

This branch contains an initial offline Slay the Spire 2 tactical-planning module added while the original AITouchGamer source archive is being integrated.

## Added in this branch
- Typed STS2 character, card, enemy-intent and combat-state models.
- Explainable local turn planner scoring lethal attacks, incoming damage, block, draw, scaling and basic debuffs.
- Branch-aware roster metadata: five main-branch characters plus The Fool isolated as experimental public-beta v0.113.0.
- JVM tests for lethal prioritisation, defensive block and energy constraints.

## Important integration status
The original AITouchGamer source archive has now been located in the user's uploaded files. It contains the app's floating controller, MediaProjection capture, OCR/OpenCV vision, Accessibility gestures, Room-based per-game learning, playbooks and offline Arabic/English teaching chat. This branch does **not yet contain that full application source**; it is an isolated tactical-engine change and must not be mistaken for a replacement app or a completed integration.

The STS2 planner currently accepts structured card and enemy properties. The generic vision pipeline has not yet been validated to extract STS2 card text, costs, enemy intents, relics, Stars, Orbs or Osty state reliably. Unknown game state is not guessed, and autonomous card execution is not enabled by this module.

## STS2 roster/version notes
The five main-branch characters are Ironclad, Silent, Regent, Necrobinder and Defect. The Fool is separated because it was introduced on the public-beta branch in v0.113.0 and is experimental. Early Access card and balance data must be versioned and refreshed as Mega Crit updates the game.

References:
- https://www.megacrit.com/news/2026-02-19-release-date-trailer/
- https://steamcommunity.com/app/2868840/announcements/
