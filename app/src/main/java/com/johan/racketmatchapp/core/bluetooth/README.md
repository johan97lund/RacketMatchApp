# bluetooth

Denna mapp hanterar all Bluetooth-logik för master/slave-kommunikation mellan enheter.

## Struktur

- `model/` – Datastrukturer som skickas mellan enheter, t.ex. `ScorePayload.kt`.
- `service/` – BluetoothService + trådar för anslutning och acceptans.
- `util/` – Hjälpklasser för konvertering, JSON, byte-arrayer etc.

## Syfte

Gör det möjligt att synka poäng i realtid mellan två enheter under en match.

## Anmärkning

Bluetoothkommunikationen är designad för enkelhet och stabilitet, och bör ej användas parallellt med annan socket-aktivitet.
