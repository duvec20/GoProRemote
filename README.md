# GoPro Control App

A Wear OS app for the Google Pixel Watch 2 that controls a GoPro camera over Bluetooth Low Energy (BLE).

BLE was chosen over Wi-Fi because it is far easier on battery for both the watch and the camera, and it covers everything a remote needs.

## Status

Work in progress — a learning project.

I'm a front-end developer, and I'm building this to teach myself Kotlin and Android (Wear OS) development. I'm using AI coding tools along the way: I decide what to build and how it should behave, and I use the AI to write and explain the Kotlin so I learn the language as the app comes together.

- [x] Request Bluetooth permissions
- [x] Scan for a GoPro (filtered by GoPro's BLE service UUID `FEA6`)
- [x] Connect / disconnect and discover services
- [x] Record button with on-watch timer
- [ ] Send real shutter commands to the camera
- [ ] Camera status (battery, SD space)
- [ ] Preset switching (video / photo / timelapse)
- [ ] Tile and complication for one-tap recording
- [ ] Auto-reconnect and haptic feedback

## Tech

- Kotlin + Jetpack Compose for Wear OS (Material 3)
- Android BLE APIs
- Min SDK 33
- Built on GoPro's [Open GoPro](https://gopro.github.io/OpenGoPro/) BLE spec

## Project layout

```
app/src/main/java/com/duvec/goproremote/
├── ble/GoProCamera.kt          # BLE scanning, connection, GATT callbacks
├── presentation/MainActivity.kt # Watch UI
├── tile/MainTileService.kt      # Wear OS tile (placeholder)
└── complication/                # Watch face complication (placeholder)
```

## Building

Open the project in Android Studio, connect a Pixel Watch 2 (or Wear OS emulator) and press Run.
