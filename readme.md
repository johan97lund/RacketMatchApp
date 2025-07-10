# 🏸 RacketMatchApp

**RacketMatchApp** är en Android-app utvecklad i Kotlin med Jetpack Compose. Appen är utformad för att användas under matcher i **Padel, Tennis, Pickleball och Badminton** – både på mobil och Wear OS-klockor. Fokus ligger på **snabb, enkel och tydlig poänghantering**, samt **Bluetooth-synk mellan spelare**.

---

## 🧩 Vad är detta projekt?

RacketMatchApp gör det enkelt att:
- Starta en ny match och välja sport
- Räkna poäng med ➕/➖
- Synka poäng i realtid via Bluetooth
- Anpassa appens utseende och funktioner efter dina behov
- Se träningsdata som steg och kaloriförbrukning under match

Den är perfekt för både spontanspelare och organiserade turneringar.

---

## ▶️ Hur man kör appen

### 📦 Förutsättningar
- Android Studio (Electric Eel eller nyare)
- Minst Android API 26 (Oreo)
- En fysisk enhet eller emulator (mobil eller Wear OS)

### 🛠️ Installationssteg

1. **Klona projektet**
   ```bash
   git clone https://github.com/johan97lund/RacketMatchApp.git
   ```

2. **Öppna i Android Studio**
    - Gå till: `File > Open`
    - Välj projektmappen

3. **Bygg projektet**
    - Tryck på "Sync Gradle" och sedan "Run"

4. **Kör appen**
    - Välj en emulator eller fysisk enhet (mobil eller klocka)

5. **(Valfritt) Lägg till Wear OS-modul**
    - `File > New Module > Wear OS Empty Activity`

---

## 🛠️ Teknologi som används

| Teknik           | Beskrivning |
|------------------|-------------|
| **Kotlin**       | Programmeringsspråk |
| **Jetpack Compose** | UI-ramverk för Android |
| **Room**         | Databas för att spara matcher |
| **DataStore**    | Spara inställningar som tema/språk |
| **Bluetooth API**| Synkronisering mellan enheter |
| **ViewModel**    | Hanterar UI-state i MVVM |
| **Wear OS**      | Appen fungerar även på smartklockor |

---

## 📚 Projektstruktur

```bash
com.johan.racketmatchapp/
├─ bluetooth/        # BluetoothService, dataklasser, utils
├─ data/
│   ├─ model/        # GameState, SportType, Team
│   ├─ repository/   # ScoreRepository, SettingsRepository
│   ├─ local/        # Room och DataStore-klasser
│   └─ util/         # Formatter, Validator
├─ ui/
│   ├─ screen/       # MainScreen, MatchScreen, etc.
│   ├─ theme/        # Theme.kt, Color.kt, Type.kt
│   ├─ components/   # Återanvändbara UI-element
│   └─ navigation/   # NavHost och Destinationer
├─ viewmodel/        # MainViewModel, MatchViewModel, SettingsViewModel
├─ settings/         # Inställnings-UI och DataStore-wrapper
└─ util/             # Constants.kt, Extensions.kt, Logger.kt
```

---

## 👤 Om utvecklaren

**Johan Lund**  
Systemutvecklare
📫 [LinkedIn](https://www.linkedin.com) | [GitHub](https://github.com/dittanvändarnamn)

---