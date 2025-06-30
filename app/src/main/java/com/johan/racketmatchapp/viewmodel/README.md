# viewmodel

Appens ViewModel-lager – ansvarar för att hantera UI-tillstånd och affärslogik.

## Struktur

- `MainViewModel.kt` – Tillstånd och logik för startskärmen.
- `MatchViewModel.kt` – Poängräkning, set, tiebreak och matchlogik.
- `SettingsViewModel.kt` – Läser och uppdaterar användarinställningar.

## Syfte

Kopplar samman UI och datalager utan att blanda logik i gränssnittet. ViewModels:

- Exponerar tillstånd via StateFlow eller LiveData
- Reagerar på förändringar från Repository
- Följer MVVM-principer
