# RECO (Android)

App móvil **Kotlin + Jetpack Compose**, tema claro/oscuro alineado al diseño en `index.html`.  
**Modo actual:** autenticación **simulada** en `AuthRepository` (sin Firebase). La pantalla **Inicio** usa la API de **TMDB** (posters con **Coil**); sin clave en `local.properties` se muestran datos demo. Firebase: `docs/FIREBASE_SETUP.md`.

## Requisitos

- Android Studio Ladybug+ (o JDK 17 + Android SDK)
- Copia `local.properties.example` a `local.properties` y define `sdk.dir` apuntando a tu Android SDK.
- Opcional: `tmdb.api.key=` con tu clave de [themoviedb.org](https://www.themoviedb.org/settings/api) para tendencias reales.

## Configuración Firebase

Sigue **[docs/FIREBASE_SETUP.md](docs/FIREBASE_SETUP.md)** para:

- Sustituir `app/google-services.json` por el de tu proyecto.
- Habilitar Email/Password, Google y Firestore.
- Opcional: `default_web_client_id` para Google Sign-In.

## Módulos y paquetes

- `com.upb.reco.app` — `MainActivity`, `RecoApplication`
- `navigation` — `Screen`, `RecoNavHost`, `MainScaffold` (tabs Inicio / Buscar / Listas / Ajustes)
- `ui/theme` — colores del diseño, tipografía, `RecoTheme`, `ThemeViewModel` + DataStore
- `ui/screens` — Splash, Login, Register, Home (`HomeViewModel` + TMDB)
- `data/repository` — `AuthRepository`, `MovieRepository`

## Compilar

```bash
./gradlew :app:assembleDebug
```

## Privacy Policy (GitHub Pages)

- Página: [Privacy Policy](https://santiagomartinez22.github.io/RecoApp/privacy-policy.html)

## Flujo

- Sin sesión: **Splash** → **Registro** o **Login** → **Main** (barra inferior: Inicio con recomendaciones TMDB; otras pestañas stub).
- Tras login/registro se limpia el back stack hasta **Main**.

El modo claro/oscuro sigue el sistema por defecto (`ThemeMode.SYSTEM`); el toggle en Ajustes se puede enlazar a `ThemeViewModel.setThemeMode` en una fase posterior.
