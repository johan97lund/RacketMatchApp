# util

Gemensamma hjälpfunktioner och konstanter som används i flera delar av appen.

## Struktur

- `Constants.kt` – Statisk konfiguration: nycklar, standardvärden.
- `Extensions.kt` – Kotlin Extension-funktioner, t.ex. `.toTiebreakString()`.
- `Logger.kt` – Förenklad loggning för utveckling och felsökning.

## Syfte

Innehåller kod som:

- Inte hör hemma i `data/`, `ui/` eller `viewmodel/`
- Underlättar kodåteranvändning
- Ökar läsbarhet och minskar duplicering
