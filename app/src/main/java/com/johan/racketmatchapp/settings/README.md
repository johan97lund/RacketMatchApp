# settings

Allt kring användarinställningar – både datahantering och gränssnitt.

## Struktur

- `data/` – DataStore-wrapper för inställningar: `UserPreferences.kt`.
- `ui/` – Inställningsskärm: språk, textstorlek, tema, regler.

## Syfte

Hanterar valbara funktioner i appen:

- Språkval
- Färg- och tema-inställningar
- Spelregler (t.ex. poänggräns i Pickleball, Americano)
- Användarpreferenser sparas lokalt med DataStore
