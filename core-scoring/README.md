# Core Scoring

This module contains the shared padel/tennis scoring engine used by both the phone app and the Wear OS app. It is the **single source of truth** for scoring rules and UI-triggering events.

## Key types

- `MatchState`: Immutable match snapshot (points, games, sets, tiebreak state).
- `ScoringAction`: Inputs sent by the UI (`PointWon`, `TiebreakDecision`).
- `ScoringEvent`: Domain events emitted by the reducer for UI reactions.

## UI guidance

- Show the tiebreak prompt when `ScoringEvent.ShowTiebreakPrompt` is emitted.
- Send `ScoringAction.TiebreakDecision(start = true/false)` based on the user's choice.
- Use `MatchState.displayPoint`, `displayGames`, and `displaySets` to render scores.
- Use `ScoringEvent.EnteredDeuce` / `EnteredAdvantage` to show in-game messages if desired.
