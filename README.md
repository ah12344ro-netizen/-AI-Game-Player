# AITouchGamer

An Android-first, local-first game assistant focused on understanding game state, planning strategy, making tactical decisions, and learning from play sessions.

## Project direction

This repository is the working home for the AITouchGamer rebuild. The initial game knowledge target is Slay the Spire 2. Game rules and strategy knowledge will remain separate from device- and board-specific screen calibration.

## Core principles

- Run gameplay analysis, decision-making, and experience storage locally on the device.
- Reuse and extend the existing touch-input and automation foundation rather than rebuilding it unnecessarily.
- Separate game knowledge, strategic reasoning, tactical decisions, and board calibration.
- Represent uncertain visual readings as unknown and avoid acting on ambiguous state.
- Explain recommendations and preserve player control.
- Do not claim that a trained on-device language model exists unless its model weights and runtime are actually included.

## Build status

The repository setup is being updated. Android build and test status will be reported only after CI has actually run and its results have been checked.
