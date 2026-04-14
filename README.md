# RECO (Android)

App móvil **Kotlin + Jetpack Compose**, tema claro/oscuro alineado al diseño en `index.html`.  
**Modo actual:** autenticación **simulada** en `AuthRepository` (sin Firebase) para compilar y probar la UI. Firebase se configura después (ver `docs/FIREBASE_SETUP.md`).

## Requisitos

- Android Studio Ladybug+ (o JDK 17 + Android SDK)
- Copia `local.properties.example` a `local.properties` y define `sdk.dir` apuntando a tu Android SDK.

## Configuración Firebase

Sigue **[docs/FIREBASE_SETUP.md](docs/FIREBASE_SETUP.md)** para:

- Sustituir `app/google-services.json` por el de tu proyecto.
- Habilitar Email/Password, Google y Firestore.
- Opcional: `default_web_client_id` para Google Sign-In.

## Módulos y paquetes

- `com.reco.app` — `MainActivity`, `RecoApplication`
- `navigation` — `Screen`, `RecoNavHost`
- `ui/theme` — colores del diseño, tipografía, `RecoTheme`, `ThemeViewModel` + DataStore
- `ui/screens` — Splash, Login, Register, Home (placeholder)
- `data/repository` — `AuthRepository`

## Compilar

```bash
./gradlew :app:assembleDebug
```

## Flujo

- Sin sesión: arranca en **Splash** → **Registro** o **Login** → **Home** (placeholder).
- Con sesión guardada: arranca en **Home**.

El modo claro/oscuro sigue el sistema por defecto (`ThemeMode.SYSTEM`); el toggle en Ajustes se puede enlazar a `ThemeViewModel.setThemeMode` en una fase posterior.
