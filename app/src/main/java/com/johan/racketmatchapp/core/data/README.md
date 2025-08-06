# data

Appens datalager – innehåller all logik för att hantera intern data, inställningar och sportlogik.

## Struktur

- `model/` – Datastrukturer: `GameState`, `SportType`, `Team`, etc.
- `repository/` – Gränssnitt mellan UI och data: t.ex. `ScoreRepository.kt`.
- `local/` – Persistens: Room, DataStore, etc.
- `util/` – Formatterare, validerare etc.

## Syfte

Separerar datalogik från presentation. Följer MVVM-principer.
