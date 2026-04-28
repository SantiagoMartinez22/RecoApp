# RECO (Android)

App móvil **Kotlin + Jetpack Compose**, tema claro/oscuro alineado al diseño en `index.html`.  
La aplicación usa **Firebase Authentication** y **Cloud Firestore** para el flujo de autenticación y persistencia del usuario, además de **TMDB** para recomendaciones, búsqueda y detalle. Los posters se cargan con **Coil**; si no hay clave de TMDB en `local.properties`, la app muestra contenido demo para mantener el flujo funcional. Para la configuración de Firebase revisa `docs/FIREBASE_SETUP.md`.

## Requisitos

- Android Studio Ladybug+ (o JDK 17 + Android SDK)
- Copia `local.properties.example` a `local.properties` y define `sdk.dir` apuntando a tu Android SDK.
- Opcional: `tmdb.api.key=` con tu clave de [themoviedb.org](https://www.themoviedb.org/settings/api) para tendencias reales.
- Archivo `google-services.json` válido para el package `com.upb.reco.app`.

## Configuración Firebase

Sigue **[docs/FIREBASE_SETUP.md](docs/FIREBASE_SETUP.md)** para:

- Sustituir `app/google-services.json` por el de tu proyecto.
- Habilitar Email/Password, Google y Firestore.
- Opcional: `default_web_client_id` para Google Sign-In.

## Módulos y paquetes

- `com.upb.reco.app` — `MainActivity`, `RecoApplication`
- `navigation` — `Screen`, `RecoNavHost`, `MainScaffold` (tabs Inicio / Buscar / Listas / Ajustes / Detalle / Créditos)
- `ui/theme` — colores del diseño, tipografía, `RecoTheme`, `ThemeViewModel` + DataStore
- `ui/screens` — Splash, Login, Register, Home, Search, Lists, Detail, Settings, Credits
- `data/repository` — `AuthRepository`, `MovieRepository`
- `data/preferences` — `UserPreferences`, `ThemePreferences`, persistencia local de usuario, favoritos y listas

## Compilar

```bash
./gradlew :app:assembleDebug
```

## Privacy Policy (GitHub Pages)

- Página: [Privacy Policy](https://santiagomartinez22.github.io/RecoApp/privacy-policy.html)

## Flujo

- Sin sesión: **Splash** → **Registro** o **Login** → **Main**.
- Dentro de la app: **Inicio** con recomendaciones TMDB, **Buscar** con filtros por género, **Listas** con favoritos y listas personalizadas, **Ajustes** con persistencia de tema/notificaciones/plataformas y acceso a créditos.
- Tras login/registro se limpia el back stack hasta **Main**.

El modo claro/oscuro sigue el sistema por defecto (`ThemeMode.SYSTEM`); el toggle en Ajustes se puede enlazar a `ThemeViewModel.setThemeMode` en una fase posterior.

## Publicación

- Package / applicationId actual: `com.upb.reco.app`
- Firma y bundle para Play Store: configurar release keystore y generar `AAB` firmado antes de subir a Google Play.

## Desarrollado por

Valeria Zuluaga Alzate  
Santiago Martínez Yara  
Miguel Aristizábal Pabón
